package com.ly.cloud.auth.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.constant.UserConstants;
import com.ly.cloud.auth.enums.UserEnums;
import com.ly.cloud.auth.excel.SystemUserEntity;
import com.ly.cloud.auth.mapper.SystemDeptMapper;
import com.ly.cloud.auth.mapper.SystemRoleMapper;
import com.ly.cloud.auth.mapper.SystemUserRoleMapper;
import com.ly.cloud.auth.po.SystemUserRolePo;
import com.ly.cloud.auth.service.SystemUserService;
import com.ly.cloud.auth.service.UserStatus;
import com.ly.cloud.auth.util.DateUtils;
import com.ly.cloud.auth.util.ExcelUtils;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.auth.dto.SystemUserDto;
import com.ly.cloud.auth.mapper.SystemUserMapper;
import com.ly.cloud.auth.po.SystemUserPo;
import com.ly.cloud.auth.vo.DeptChildParentIdPairVO;
import com.ly.cloud.auth.vo.SystemUserVo;
import com.ly.cloud.backup.dto.UsersExportDto;
import com.ly.cloud.backup.util.Sm4Util;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserPo> implements SystemUserService {
    private static final Logger logger = LoggerFactory.getLogger(SystemUserServiceImpl.class);

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private SystemUserRoleMapper systemUserRoleMapper;

    @Autowired
    private SystemRoleMapper systemRoleMapper;

    @Autowired
    private SystemDeptMapper systemDeptMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemUserDto systemUserDto) {
        try {
            // 判断当前用户账号 系统是否存在
            List<SystemUserPo> systemUserPos = systemUserMapper.selectList(new QueryWrapper<SystemUserPo>().lambda().eq(SystemUserPo::getUserName, systemUserDto.getUserName()));
            if (StringUtils.isNotEmpty(systemUserPos)) {
                throw new RuntimeException("当前用户账号已存在，请勿重复添加！");
            }
            SystemUserPo systemUserPo = new SystemUserPo();
            BeanUtils.copyProperties(systemUserDto, systemUserPo);
            systemUserPo.setPassword(Sm4Util.sm4EcbEncrypt(systemUserPo.getPassword()));

            systemUserPo.setCreateBy("admin");
            systemUserPo.setCreateTime(new Date());
            int result = systemUserMapper.insert(systemUserPo);

            // 将用户与角色信息插入关联表中
            if (systemUserDto.getRoleId() != null) {
                SystemUserRolePo systemUserRolePo = new SystemUserRolePo();
                systemUserRolePo.setRoleId(systemUserDto.getRoleId());
                systemUserRolePo.setUserId(systemUserPo.getUserId());
                systemUserRoleMapper.insert(systemUserRolePo);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SystemUserDto systemUserDto) {
        try {
            // 用于不修改密码，只修改密码时间
            if (systemUserDto.getUserId() == null) {
                SystemUserVo systemUserVo = systemUserMapper.selectBydto(systemUserDto);
                if (systemUserVo != null) {
                    systemUserDto.setUserId(systemUserVo.getUserId());
                }
            }
            SystemUserPo systemUserPo = new SystemUserPo();
            BeanUtils.copyProperties(systemUserDto, systemUserPo);

            if (StringUtils.isNotEmpty(systemUserPo.getPassword())) {
                systemUserPo.setPassword(Sm4Util.sm4EcbEncrypt(systemUserPo.getPassword()));
                // 设置密码修改日期
                systemUserPo.setPasswordTime(new Date());
            }
            systemUserPo.setUpdateBy("admin");
            systemUserPo.setUpdateTime(new Date());
            int result = systemUserMapper.updateById(systemUserPo);

            // 将用户与角色信息插入关联表中
            if (systemUserDto.getRoleId() != null) {

                // 先删除所有跟用户id 关联的角色信息
                systemUserRoleMapper.delete(new QueryWrapper<SystemUserRolePo>().lambda().eq(SystemUserRolePo::getUserId, systemUserDto.getUserId()));

                SystemUserRolePo systemUserRolePo = new SystemUserRolePo();
                systemUserRolePo.setRoleId(systemUserDto.getRoleId());
                systemUserRolePo.setUserId(systemUserPo.getUserId());
                systemUserRoleMapper.insert(systemUserRolePo);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassword(SystemUserDto systemUserDto) {
        try {
            SystemUserPo systemUserPo = new SystemUserPo();
            BeanUtils.copyProperties(systemUserDto, systemUserPo);

            SystemUserVo systemUserVo = systemUserMapper.selectBydto(systemUserDto);

            if (systemUserVo == null) {
                throw new RuntimeException("账户不存在，修改密码失败！");
            }

            if (!Sm4Util.sm4EcbEncrypt(systemUserDto.getOldPassword()).equals(systemUserVo.getPassword())) {
                throw new RuntimeException("旧密码错误，修改密码失败！");
            }

            if (StringUtils.isNotEmpty(systemUserPo.getPassword())) {
                systemUserPo.setPassword(Sm4Util.sm4EcbEncrypt(systemUserPo.getPassword()));
            }
            if (StringUtils.isNotEmpty(systemUserPo.getPassword())) {
                systemUserPo.setUserId(systemUserVo.getUserId());
            }

            if (systemUserPo.getUserId() == null) {
                systemUserPo.setUserId(systemUserVo.getUserId());
            }

            systemUserPo.setUpdateBy("admin");
            systemUserPo.setUpdateTime(new Date());
            // 设置密码修改日期
            systemUserPo.setPasswordTime(new Date());
            int result = systemUserMapper.updateById(systemUserPo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public SystemUserVo find(Long id) {
        return systemUserMapper.selectById(id);
    }

    @Override
    public IPage<SystemUserVo> query(Integer page, Integer pageSize, String deptParentId, String content, String status) {
        try {
            Page<SystemUserVo> sPage = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize : 15);
            Long lDeptId = Long.valueOf(deptParentId);
            // 查询部门下所有的子部门Id
            List<Long> childDeptIdList = new ArrayList<>();
            if(StringUtils.isNotEmpty(deptParentId)) {
                List<DeptChildParentIdPairVO> list = systemDeptMapper.queryChildAndParent();
                childDeptIdList=getAllChildrenDeptList(lDeptId,list);
            }
            SystemUserDto systemUserDto = new SystemUserDto();
            if (StringUtils.isNotEmpty(deptParentId)) systemUserDto.setDeptId(lDeptId);
            if (StringUtils.isNotEmpty(content)) systemUserDto.setContent(content);
            if (StringUtils.isNotEmpty(status)) systemUserDto.setStatus(status);
            return systemUserMapper.select(sPage, systemUserDto, childDeptIdList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private List<Long> getAllChildrenDeptList(Long deptParentId, List<DeptChildParentIdPairVO> list) {
        List<Long> retLi=new ArrayList<>();
        retLi.add(deptParentId);
        for(DeptChildParentIdPairVO e:list){
            if(e.getParentDeptId()!=null&& e.getParentDeptId().equals(deptParentId)){
                retLi.add(e.getDeptId());
            }
            for(Long l:retLi){
                if(e.getParentDeptId()!=null&& e.getParentDeptId().equals(l)){
                    retLi.add(e.getDeptId());
                    break;
                }
            }
        }
        return retLi;
    }

    @Override
    public void passwordEncryption() {
        List<SystemUserPo> systemUserPos = systemUserMapper.selectList(new QueryWrapper<>());
        if (StringUtils.isNotEmpty(systemUserPos)) {
            systemUserPos.forEach(item -> {
                String oldPassword = new String(item.getPassword());
                if (StringUtils.isEmpty(oldPassword)) {
                    oldPassword = "Ly123456";
                }
//                if (!(oldPassword.substring(0, 7).equals("$2a$10$") || oldPassword.length() >= 30)) {
//                    item.setPassword(Sm4Util.sm4EcbEncrypt(oldPassword));
//                }
                if (oldPassword.length() < 32) {
                    try {
                        item.setPassword(Sm4Util.sm4EcbEncrypt(oldPassword));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("密码加密失败！");
                    }
                }

                // 如果旧密码不等于现在的密码，则进行修改密码
                if (!oldPassword.equals(item.getPassword())) {
                    item.setUpdateBy("admin");
                    item.setUpdateTime(new Date());
                    systemUserMapper.updateById(item);
                }
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        try {
            for (Long id : ids) {
                int result = systemUserMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importExcel(MultipartFile file) {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 判断文件是否为excel
        Boolean checkExcel = ExcelUtils.validateExcel(fileName);
        if (!checkExcel) {
            throw new RuntimeException("文件格式不正确，请选择后缀名为.xls或.xlsx的文件");
        }
        // 判断文件是xls还是xlsx
        boolean excelXls = true;
        if (ExcelUtils.isExcelXlsx(fileName)) {
            excelXls = false;
        }
        InputStream in = null;
        Workbook workbook = null;
        // 读取文件并创建对应workbook
        try {
            in = file.getInputStream();
            if (excelXls) {
                workbook = new HSSFWorkbook(in);
            } else {
                workbook = new XSSFWorkbook(in);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException("文件读取失败，请重试");
        } finally {
            //关闭资源
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<SystemUserVo> userList = new ArrayList<SystemUserVo>();
        // 获取sheet
        Sheet sheet = workbook.getSheetAt(0);
        // sheet有数据的总行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        // sheet的列数(选用第一行标题列确定)
        int totalColumns = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalColumns = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        // 循环Excel行数
        for (int i = 1; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            // 判断行的数据是否为空，如果为空则直接跳过
            if (ExcelUtils.isRowEmpty(row)){
                continue;
            }
            SystemUserVo user = new SystemUserVo();
            // 循环Excel的列，依次为：姓名(nick_name)、用户账号(user_name)、密码(password)、
            // 邮箱(email)、手机号码(phoneNumber)、角色(roleName)、状态(status)
            for (int j = 0; j < totalColumns; j++) {
                Cell cell = row.getCell(j);
                if (null != cell) {
                    if (j == 0) {
                        user.setNickName(cell.getStringCellValue());
                    }
                    else if (j == 1){
                        user.setUserName(cell.getStringCellValue());
                    }
                    else if (j == 2){
                        user.setPassword(cell.getStringCellValue());
                    }
                    else if (j == 3){
                        user.setEmail(cell.getStringCellValue());
                    }
                    else if (j == 4){
                        user.setPhonenumber(cell.getStringCellValue());
                    }
                    else if (j == 5){
                        user.setRoleName(cell.getStringCellValue());
                    }
                    else if (j == 6){
                        user.setStatus(cell.getStringCellValue());
                    }
                }
            }
            //将excel解析出来的数据赋值给对象后添加到list中
            userList.add(user);
        }
        // 成功与失败数记录
        int successNum = 0;
        int failNum = 0;
        int rowNum = 0;
        StringBuilder res = new StringBuilder();
        res.append("其中失败的有：\n");
        // 保存用户数据
        if (StringUtils.isNotEmpty(userList)) {
            for (SystemUserVo systemUserVo:userList){
                rowNum++;
                //转换成数据库实体类
                SystemUserPo systemUserPo = new SystemUserPo();
                SystemUserRolePo systemUserRolePo = new SystemUserRolePo();
                try {
                    // 姓名(nick_name)、用户账号(user_name)、密码(password)、
                    // 邮箱(email)、手机号码(phoneNumber)、角色(roleId)、状态(status)
                    // 依次将systemUserVo的数据传递给systemUserPo
                    if (StringUtils.isNotEmpty(systemUserVo.getNickName())) {
                        systemUserPo.setNickName(systemUserVo.getNickName());
                    } else {
                        failNum++;
                        res.append("第"+rowNum+"行:"+systemUserVo.getNickName()+"，姓名为空\n");
                        continue;
                    }
                    if (StringUtils.isNotEmpty(systemUserVo.getUserName())){
                        // 判断当前用户账号 系统是否存在
                        List<SystemUserPo> systemUserPos = systemUserMapper.selectList(new QueryWrapper<SystemUserPo>().lambda().eq(SystemUserPo::getUserName, systemUserVo.getUserName()));
                        if (StringUtils.isNotEmpty(systemUserPos)) {
                            failNum++;
                            res.append("第"+rowNum+"行:"+systemUserVo.getNickName()+"，账号已存在\n");
                            continue;
                        }
                        systemUserPo.setUserName(systemUserVo.getUserName());
                    } else {
                        failNum++;
                        res.append("第"+rowNum+"行:"+systemUserVo.getNickName()+"，账号为空\n");
                        continue;
                    }
                    if (StringUtils.isNotEmpty(systemUserVo.getPassword())){
                        systemUserPo.setPassword(Sm4Util.sm4EcbEncrypt(systemUserVo.getPassword()));
                    } else {
                        failNum++;
                        res.append("第"+rowNum+"行:"+systemUserVo.getNickName()+"，密码为空\n");
                        continue;
                    }
                    systemUserPo.setEmail(systemUserVo.getEmail());
                    systemUserPo.setPhonenumber(systemUserVo.getPhonenumber());
                    systemUserPo.setStatus("正常".equals(systemUserVo.getStatus())? UserConstants.NORMAL : UserConstants.EXCEPTION);
                    // TODO：默认将用户的部门设置为ID为1的部门，正式环境后根据实际业务修改
                    systemUserPo.setDeptId(1L);
                    this.save(systemUserPo);
                    //根据角色名称获取角色Id
                    Long roleId = null;
                    if (StringUtils.isNotEmpty(systemUserVo.getRoleName())) {
                        roleId = systemRoleMapper.selectRoleIdByRoleName(systemUserVo.getRoleName());
                    }
                    //将用户Id和角色Id存入用户-角色关联表ly_sys_user_role
                    if ( roleId != null || roleId.longValue() != 0) {
                        systemUserRolePo.setRoleId(roleId);
                        systemUserRolePo.setUserId(systemUserPo.getUserId());
                        systemUserRoleMapper.insert(systemUserRolePo);
                    }
                    successNum++;
                }catch (Exception e){
                    logger.info("用户导入异常"+e.getMessage());
                    failNum++;
                    res.append("第"+rowNum+"行:"+systemUserVo.getNickName()+"，用户数据读取异常\n");
                }
            }
            String result = "本次导入"+userList.size()+"条用户信息，成功"+successNum+"条，失败"+failNum+"条。\n";
            if (failNum>0) {
                result += res;
            }
            return result;
        }
        String msg = "导入数据为空或无法识别文件，请确认！";
        return msg;
    }

    /**
     * 动态生成用户导入模板
     */
    @Override
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 列标题
        String[] title = {"*序号","*姓名","*用户账号","*密码","*所属部门","*性别","邮箱","手机号码","*角色","*状态"};
        // 获取所有部门并转换成数组
        List<String> deptList = systemDeptMapper.queryDeptNameAll();
        String[] deptName = deptList.toArray(new String[deptList.size()]);
        // 性别
        String[] sex = {"男","女","未知"};
        // 获取所有角色名称并转换成数组
        List<String> roleList = systemRoleMapper.selectRoleNameAll();
        String[] roleName = roleList.toArray(new String[roleList.size()]);
        // 状态
        String[] status = {"正常","停用"};
        // 下拉框数据
        List<String[]> downData = new ArrayList();
        downData.add(deptName);
        downData.add(sex);
        downData.add(roleName);
        downData.add(status);
        // 下拉的列序号数组
        String [] downColumns = {"4","5","8","9"};
        // 获取需要批注的列下标集合
        List<Integer> commentsCellIndex = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            if (title[i].indexOf("*") != -1){
                commentsCellIndex.add(i);
            }
        }
        OutputStream out = null;
        try {
            // 生成含标题和下拉框的XSSFWorkbook
            XSSFWorkbook workbook = ExcelUtils.createExcelTemplate(title, downData, downColumns);
            // 标题表格追加批注
            String comment = "带\"*\"号为必填项";
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            for (int i = 0; i < commentsCellIndex.size(); i++) {
                int col = commentsCellIndex.get(i);
                ExcelUtils.setCommentsOfPoiCell(0, col, row.getCell(col), row, comment);
            }
//            ExcelUtils.setCommentsOfPoiCell(0,1,row.getCell(0),row,comment);
            // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 文件名
            String fileName = "用户信息导入导出模板"+DateUtils.dateTime();
            fileName = URLEncoder.encode(fileName, "UTF-8");
            // 设置文件头
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 通过response获取OutputStream对象(out)
            out = new BufferedOutputStream(response.getOutputStream());
            // 数据写入输出流
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            logger.error("用户导入导出模板下载异常：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("用户导入导出模板下载失败！");
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void export(UsersExportDto usersExportDto, HttpServletResponse response) {
        List<Long> userIds = usersExportDto.getUserIds();
        Long deptId = usersExportDto.getDeptId();
        List<SystemUserVo> users = new ArrayList<>();
        List<SystemUserEntity> systemUserEntities = new ArrayList<>();
        // 判断是否有勾选要导出的用户数据，没有的话导出选择部门下的所有用户(含子部门)
        if (userIds != null && userIds.size()>0) {
            // 有勾选用户，导出勾选部分用户数据
            users = systemUserMapper.queryByIds(userIds);
        } else {
            // 没有勾选用户，导出选择部门下的所有用户(含子部门)
            // 查询选择部门下所有的子部门Id
            List<Long> childDeptIdList = new ArrayList<>();
            if(deptId != null && deptId.longValue() != 0) {
                List<DeptChildParentIdPairVO> list = systemDeptMapper.queryChildAndParent();
                childDeptIdList=getAllChildrenDeptList(deptId,list);
            }
            users = systemUserMapper.selectByDeptIds(childDeptIdList);
        }
        if (users != null && users.size()>0) {
            for (int i = 0; i < users.size(); i++) {
                SystemUserVo user = users.get(i);
                SystemUserEntity entity = new SystemUserEntity();
                BeanUtils.copyProperties(user, entity);
                // 转码
                // 性别 (0男 1女 2未知)
                entity.setSex(UserEnums.getValue(entity.getSex()));
                // 用户状态 (0正常 1停用 2删除)
                entity.setStatus(UserStatus.getInfo(entity.getStatus()));
                // 勾选用户的情况下，序号为用户的显示顺序；无勾选的情况下，序号为自动递增的虚拟序号
                if (userIds != null && userIds.size()>0) {
                    entity.setUserSort(user.getUserSort());
                } else {
                    entity.setUserSort(i+1);
                }
                systemUserEntities.add(entity);
            }
        }
        /*List<SystemUserEntity> systemUserEntities = users.stream().map(user ->{
            SystemUserEntity entity = new SystemUserEntity();
            BeanUtils.copyProperties(user, entity);
            return entity;
        }).collect(Collectors.toList());*/
        //导出设置
        ExportParams params = new ExportParams();
//        params.setCreateHeadRows(true);
//        params.setTitle("水表信息");
        params.setType(ExcelType.XSSF);
        //导出
        Workbook workbook = ExcelExportUtil.exportExcel(params, SystemUserEntity.class, systemUserEntities);
//        // 列标题
//        String[] title = {"姓名","用户账号","密码","邮箱","手机号码","角色","状态"};
        // 文件名
        String fileName = "用户信息"+DateUtils.dateTime()+".xlsx";
        // 表名
        String sheetName = "用户数据";
//        // 创建工作簿
//        HSSFWorkbook workbook = ExcelUtils.createHSSFWorkbook(sheetName,title);
//        // 导出用户数据集合，写到Excel
//        List<String[]> userList = new ArrayList<String[]>();
//        // 用户ID集合
//        List<Long> userIds = usersExportDto.getUserIds();
//        // 所选部门Id
//        Long deptId = usersExportDto.getDeptId();
//        // 判断是否有勾选要导出的用户数据，没有的话导出选择部门下的所有用户(含子部门)
//        if (userIds != null && userIds.size()>0) {
//            // 有勾选用户，导出勾选部分用户数据
//            for (Long userId : userIds) {
//                SystemUserVo systemUserVo = systemUserMapper.selectById(userId);
//                String[] user = new String[]{systemUserVo.getNickName(),systemUserVo.getUserName(),systemUserVo.getPassword(),
//                        systemUserVo.getEmail(),systemUserVo.getPhonenumber(),systemUserVo.getRoleName(),"0".equals(systemUserVo.getStatus())?"正常":"停用"};
//                userList.add(user);
//            }
//        } else {
//            // 没有勾选用户，导出选择部门下的所有用户(含子部门)
//            // 查询选择部门下所有的子部门Id
//            List<Long> childDeptIdList = new ArrayList<>();
//            if(deptId != null && deptId.longValue() != 0) {
//                List<DeptChildParentIdPairVO> list = systemDeptMapper.queryChildAndParent();
//                childDeptIdList=getAllChildrenDeptList(deptId,list);
//            }
//            List<SystemUserVo> systemUserVos = systemUserMapper.selectByDeptIds(childDeptIdList);
//            if (systemUserVos != null && systemUserVos.size()>0) {
//                for (SystemUserVo systemUserVo : systemUserVos) {
//                    String[] user = new String[]{systemUserVo.getNickName(),systemUserVo.getUserName(),systemUserVo.getPassword(),
//                            systemUserVo.getEmail(),systemUserVo.getPhonenumber(),systemUserVo.getRoleName(),"0".equals(systemUserVo.getStatus())?"正常":"停用"};
//                    userList.add(user);
//                }
//            }
//        }
        // 获取表开始填充数据
//        Sheet sheet = workbook.getSheetAt(0);
//        // 行号，标题占用了第一行，数据内容从第2行开始
//        int i = 1;
//        Row row;
//        if (userList != null && userList.size()>0){
//            for (String[] user : userList) {
//                row = sheet.createRow(i);
//                // 循环填充单元格数据
//                for (int j = 0; j<user.length; j++) {
//                    Cell cell = row.createCell(j);
//                    cell.setCellValue(user[j]);
//                }
//                i++;
//            }
//        }
        // 声明输出流响应到客户端
        OutputStream outputStream = null;
        try {
            // 设置文件ContentType类型
            response.setContentType("application/octet-stream;charset=UTF-8");
            // 设置文件头
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 获取输出流
            outputStream = response.getOutputStream();
            // 文档写入输出流
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logger.error("用户导出异常：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("用户导出失败！");
        } finally {
            //关闭输出流
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
