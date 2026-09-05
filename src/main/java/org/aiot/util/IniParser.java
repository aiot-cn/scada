package org.aiot.util;

import org.nutz.lang.Strings;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IniParser {
    // 存储所有配置：外层 Map 的 key 是 section 名，value 是该 section 下的键值对
    private final Map<String, Map<String, String>> sections = new LinkedHashMap<>();
    // 当前正在解析的 section（默认空字符串表示无 section）
    private String currentSection = "";

    /**
     * 构造方法，直接加载文件
     */
    public IniParser(File file){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造方法，从输入流加载（例如 Classpath 资源）
     */
    public IniParser(InputStream inputStream){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心解析逻辑
     */
    private void parse(BufferedReader reader) throws IOException {
        String line;
        int lineNum = 0;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            String trimmed = line.trim();
            // 忽略空行和注释行（; 或 # 开头）
            if (trimmed.isEmpty() || trimmed.startsWith(";") || trimmed.startsWith("#")) {
                continue;
            }
            // 处理 Section：[section]
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                currentSection = trimmed.substring(1, trimmed.length() - 1).trim();
                // 如果 section 名重复，会覆盖之前的内容（可根据需要修改）
                sections.putIfAbsent(currentSection, new LinkedHashMap<>());
                continue;
            }
            // 处理键值对：支持 = 或 : 作为分隔符，取第一个分隔符
            int sepIdx = -1;
            if ((sepIdx = trimmed.indexOf('=')) == -1) {
                sepIdx = trimmed.indexOf(':');
            }
            if (sepIdx == -1) {
                // 既没有 = 也没有 :，视为非法行，跳过或可抛出异常
                System.err.println("Warning: invalid line " + lineNum + ": " + line);
                continue;
            }
            String key = trimmed.substring(0, sepIdx).trim();
            String value = trimmed.substring(sepIdx + 1).trim();
            // 如果当前 section 还没创建（正常情况下应该已存在）
            sections.computeIfAbsent(currentSection, k -> new LinkedHashMap<>());
            sections.get(currentSection).put(key, value);
        }
    }

    /**
     * 获取指定 section 下的某个 key 的值
     */
    public String get(String section, String key) {
        Map<String, String> sectionMap = sections.get(section);
        return sectionMap == null ? null : sectionMap.get(key);
    }

    public Integer getInteger(String section,String key,Integer defaultValue){
        String value = get(section, key);
        if(Strings.isBlank(value))
            return defaultValue;
        return Integer.parseInt(value);
    }

    /**
     * 获取指定 section 下的所有键值对
     */
    public Map<String, String> getSection(String section) {
        return sections.get(section);
    }

    /**
     * 获取所有 section 名称（顺序保持与文件一致）
     */
    public Set<String> getSectionNames() {
        return sections.keySet();
    }

    /**
     * 获取整个配置数据（只读副本）
     */
    public Map<String, Map<String, String>> getAll() {
        return Collections.unmodifiableMap(sections);
    }

}
