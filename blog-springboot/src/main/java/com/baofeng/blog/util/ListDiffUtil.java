package com.baofeng.blog.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ListDiffUtil {
    /**
     * 比较旧列表和新列表，返回新增和删除的元素
     * @param oldList 旧列表（已有数据）
     * @param newList 新列表（更新请求）
     * @return Map 包含 "toAdd" 和 "toRemove" 两个 key，对应新增和删除的元素列表
     */
    public static Map<String, List<String>> diffList(List<String> oldList, List<String> newList) {
        // 使用 Set 提高效率
        Set<String> oldSet = oldList == null ? Collections.emptySet() : new HashSet<>(oldList);
        Set<String> newSet = newList == null ? Collections.emptySet() : new HashSet<>(newList);

        // 新增 = newList - oldList
        List<String> toAdd = newSet.stream()
                .filter(item -> !oldSet.contains(item))
                .collect(Collectors.toList());

        // 删除 = oldList - newList
        List<String> toRemove = oldSet.stream()
                .filter(item -> !newSet.contains(item))
                .collect(Collectors.toList());

        Map<String, List<String>> result = new HashMap<>();
        result.put("toAdd", toAdd);
        result.put("toRemove", toRemove);
        return result;
    }
}
