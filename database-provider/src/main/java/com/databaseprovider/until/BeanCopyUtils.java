package com.databaseprovider.until;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;


/**
 * 工具类：只复制非空属性
 */
public class BeanCopyUtils {
    /**
     * 将 source 对象中的非空属性复制到 target 对象中
     * @param source 源对象（B）
     * @param target 目标对象（A）
     */
    public static void copyNonNullProperties(Object source ,Object target){
        BeanUtils.copyProperties(source,target,getNullPropertyNames(source));
    }
    private static String[] getNullPropertyNames(Object source){
        final BeanWrapper src =new BeanWrapperImpl(source);
        PropertyDescriptor[] pds=src.getPropertyDescriptors();
        Set<String> emptyNames=new HashSet<>();
        for(PropertyDescriptor pd:pds){
            Object srcValue=src.getPropertyValue(pd.getName());
            if (srcValue==null){
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[0]);
    }
}
