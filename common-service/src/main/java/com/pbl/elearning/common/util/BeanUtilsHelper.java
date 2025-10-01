package com.pbl.elearning.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public final class BeanUtilsHelper {
    /**
     * Copy các property từ source sang target, bỏ qua những property null trong source
     */
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * Lấy ra danh sách tên property có giá trị null trong object
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            String propName = pd.getName();
            Object value = src.getPropertyValue(propName);
            if (value == null) {
                emptyNames.add(propName);
            }
        }
        return emptyNames.toArray(new String[0]);
    }

}