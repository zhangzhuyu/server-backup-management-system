package com.ly.cloud.backup.util;

import com.ly.cloud.auth.dto.LoginUser;
import com.ly.cloud.auth.po.SysUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeptIdUtil {
//    public static Long deptId(HttpServletRequest request){
//        String s=deptIdStr(request);
//        System.out.println(s);
//        LoginUser user=(LoginUser) request.getAttribute("user");
//        if (user!=null){
//            return user.getDeptId();
//        }
//        return null;
//    }

    public static String deptId(HttpServletRequest request){
        String deptIdList=request.getHeader("Deptidlist");
        if(deptIdList==null||"".equals(deptIdList)||"null".equals(deptIdList)||deptIdList.contains("object")){
            //当未传部门时检测是否是超级管理员，如果是超，是要默认返回全部资源信息的，如果有超管有传的话，按正常走 --2023-11-29 改为统一登陆时获取一次设置减少后台计算量
//            LoginUser user= (LoginUser) request.getAttribute("user");
//            if(user.getUser().getUserName().equals("superadmin")){
//                List<SystemDeptPo>  pos=mapper.selectList(null);
//                if(pos!=null && !pos.isEmpty()){
//                    List<Long> ids=pos.stream().map(u->u.getDeptId()).collect(Collectors.toList());
//                    ids.add(-1L);
//                    return joinLong("|",ids);
//                }
//            }
            //不是超级管理员返回null
            return null;
        }
        return deptIdList.replaceAll(",","|")+"|-1"; //直接返回供regexp 使用的格式: '1729301157234970626|1727203497556324354|-1'
    }

    public static String join(String delimiter,List<String> list){
        String s="";
        for(String l:list){
            s+=l+delimiter;
        }
        s=s.substring(0,s.length()-1);
        return s;
    }

    public static String joinLong(String delimiter, List<Long> list) {
        String s="";
        for(Long l:list){
            s+=l+delimiter;
        }
        s=s.substring(0,s.length()-1);
        return s;
    }

    public static List<String> getAuthDeptIds(String authDeptId) {
        if (authDeptId==null) return null;
        List<String> list=new ArrayList<>();
        String[] s=authDeptId.split(",");
        if(s.length>0){
            return Arrays.asList(s);
        }
//        if(s.length>0){
//            for(String a:s){
//                try{
//                    Long l=Long.parseLong(a);
//                    list.add(l);
//                }catch (Exception e){
//                    //不处理
//                }
//            }
//        }
        return list;
    }

    public static String monitorDept(HttpServletRequest request) {
        LoginUser user=(LoginUser) request.getAttribute("user");
        if(user!=null&&user.getUser()!=null){
            SysUser sysUser=user.getUser();
            if(sysUser.getUserName().equals("superadmin")) return null; //超管不控制
            return deptId(request);
        }
        return null;
    }

//    public static void main(String[] args){
//        List<Long> list= Arrays.asList(1L, 2L, 3L, 4L);
//        String result = join(",", list);
//        System.out.println(result);  // 输出：1,2,3,4
//    }


}


//    List<SystemUserPo> systemUserPos = systemUserMapper.selectList(new QueryWrapper<SystemUserPo>()
//            .lambda().eq(SystemUserPo::getUserName, loginBody.getUsername()));
//    Long deptId=null;
//        if(StringUtils.isNotEmpty(systemUserPos)){
//                deptId = systemUserPos.get(0).getDeptId();
//                }