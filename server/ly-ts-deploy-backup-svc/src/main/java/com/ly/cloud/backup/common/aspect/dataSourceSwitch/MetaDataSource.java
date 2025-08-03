package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

/**
 * @author SYC
 * @Date: 2022/3/8 10:54
 * @Description 数据源切换
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ly_rm_database")
public class MetaDataSource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源编号SJYBH
     */
    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String sjybh;

    /**
     * 系统编号XTBH
     */
    @TableField(exist = false)
    private String xtbh;


    @TableField("name")
    private String sjymc;

    /**
     * 数据源URL
     */
    @TableField("url")
    private String sjyurl;

    /**
     * 数据库IPSJKIP
     */
    @TableField("ipv4")
    private String sjkip;


    /**
     * 数据源端口SJKDK
     */
    @TableField("port")
    private String sjkdk;

    /**
     * 数据库用户SJKYH
     */
    @TableField("user")
    private String sjkyh;


    /**
     * 数据库密码SJKMM
     */
    @TableField("password")
    private String sjkmm;

    /**
     * 数据库类型SJYLX
     */
    @TableField("source_type")
    private String sjylx;

    /**
     * 数据源驱动SJYQD
     */
    @TableField("driver")
    private String sjyqd;


    /**
     * 备注信息BZXX
     */
    @TableField(exist = false)
    private String bzxx;

    /**
     * 测试连接状态CSLJZT，正常是1，否则报错连接错误信息
     */
    @TableField("test_status")
    private String csljzt;

    /**
     * 数据库名称SJKKMC
     */
    @TableField("database_name")
    private String sjkkmc;



    /**
     * 操作人CZR
     */
    @TableField("operator_id")
    private String czr;

    /**
     * 操作时间CZSJ
     */
    @TableField("operation_time")
    private Date czsj;

    @TableField(exist = false)
    private String sfsh;


    /**
     * dts数据源编号
     */
    @TableField(exist = false)
    private String etlsjybh;

    /**
     * 数据源业务类型SJYYWBH
     */
    @TableField(exist = false)
    private String sjyywbh;

    @TableField(exist = false)
    private String qjklx;



    @TableField(exist = false)
    private List<String> schemaList;

    @TableField(exist = false)
    private List<String> sjkkmcList;

    @TableField(exist = false)
    private List<Map<String,String>> lschemaList;


    @TableField(exist = false)
    private String systemName;

    @TableField(exist = false)
    private String etlsjylx;

    @TableField(exist = false)
    private String key;




    public String gainSystemId(){
        return  this.getXtbh() != null ? this.getXtbh() : this.getSjybh();
    }

    public String gainSystemName(){
        return  this.getSystemName() != null ? this.getSystemName() : this.getSjymc();
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaDataSource that = (MetaDataSource) o;
        /*return
                ("|"+sjkdk+"|"+sjkip+"|"+sjkkmc+"|"+sjkyh+"|")
                        .equals("|"+that.sjkdk+"|"+that.sjkip+"|"+that.sjkkmc+"|"+that.sjkyh+"|");*/
        return Objects.equals(sjkdk, that.sjkdk) &&
                Objects.equals(sjkip, that.sjkip) &&
                Objects.equals(sjkkmc, that.sjkkmc) &&
                Objects.equals(sjkyh, that.sjkyh);
    }

    /*@Override
    public int hashCode() {
        String s = "|"+sjkdk+"|"+sjkip+"|"+sjkkmc+"|"+sjkyh+"|";
        return Objects.hash(s);
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(sjkdk, sjkip, sjkkmc, sjkyh);
    }


    public static boolean judgeSame(MetaDataSource metaDataSource1, MetaDataSource metaDataSource2) {
        if (metaDataSource1.getSjkip().equals(metaDataSource2.getSjkip())&&
                metaDataSource1.getSjkdk().equals(metaDataSource2.getSjkdk())&&
                metaDataSource1.getSjkyh().equals(metaDataSource2.getSjkyh())
        ){
            return true;
        } else {
            return false;
        }

    }



    public static Boolean judgeYWKSame(MetaDataSource metaDataSource1,MetaDataSource metaDataSource2){
        List<String> sjkkmcList = metaDataSource1.getSjkkmcList();
        List<String> sjkkmcList2 = metaDataSource2.getSjkkmcList();
        sjkkmcList.sort(Comparator.comparing(String::hashCode));
        sjkkmcList2.sort(Comparator.comparing(String::hashCode));
        if (metaDataSource1.getSjkip().equals(metaDataSource2.getSjkip())&&
                metaDataSource1.getSjkdk().equals(metaDataSource2.getSjkdk())&&
                metaDataSource1.getSjkyh().equals(metaDataSource2.getSjkyh())&&
                sjkkmcList.toString().equals(sjkkmcList2.toString())
        ){
            return true;
        } else {
            return false;
        }
    }

    public static Boolean judgeOtherSame(MetaDataSource metaDataSource1,MetaDataSource metaDataSource2){
        List<String> sjkkmcList = new ArrayList<>();
        List<String> sjkkmcList2 = new ArrayList<>();
        if ("ORACLE".equals(metaDataSource1.getSjylx())){
            sjkkmcList.addAll(metaDataSource1.getSchemaList());
        } else {
            sjkkmcList.addAll(metaDataSource1.getSjkkmcList());
        }
        if ("ORACLE".equals(metaDataSource2.getSjylx())){
            sjkkmcList2.addAll(metaDataSource2.getSchemaList());
        } else {
            sjkkmcList2.addAll(metaDataSource2.getSjkkmcList());
        }

        sjkkmcList.sort(Comparator.comparing(String::hashCode));
        sjkkmcList2.sort(Comparator.comparing(String::hashCode));
        if (metaDataSource1.getSjkip().equals(metaDataSource2.getSjkip())&&
                metaDataSource1.getSjkdk().equals(metaDataSource2.getSjkdk())&&
                metaDataSource1.getSjkyh().equalsIgnoreCase(metaDataSource2.getSjkyh())&&
                sjkkmcList.removeAll(sjkkmcList2)
        ){
            return true;
        } else {
            return false;
        }
    }

    public static Boolean judgeQJKSame(MetaDataSource etlSource,MetaDataSource qjkSource){
        List<String> sjkkmcList = new ArrayList<>();
        List<String> sjkkmcList2 = new ArrayList<>();
        if ("ORACLE".equals(etlSource.getSjylx())){
            sjkkmcList.addAll(etlSource.getSchemaList());
        } else {
            sjkkmcList.addAll(etlSource.getSjkkmcList());
        }
        if ("ORACLE".equals(qjkSource.getSjylx())){
            sjkkmcList2.addAll(qjkSource.getSchemaList());
        } else {
            sjkkmcList2.addAll(qjkSource.getSjkkmcList());
        }

        sjkkmcList.sort(Comparator.comparing(String::hashCode));
        sjkkmcList2.sort(Comparator.comparing(String::hashCode));
        if (etlSource.getSjkip().equals(qjkSource.getSjkip()) && etlSource.getSjkdk().equals(qjkSource.getSjkdk())) {
            for (String etlSjkkmc : sjkkmcList) {
                for (String qjkSjkkmc : sjkkmcList2) {
                    if (etlSjkkmc.equals(qjkSjkkmc)){
                        return true;
                    }
                }
            }
        }
        return false;
    }



    @TableField(exist = false)
    private List<String> etlIdList;



}
