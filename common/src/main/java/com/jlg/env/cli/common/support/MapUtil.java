package com.jlg.env.cli.common.support;

import java.util.Map;

public class MapUtil {
  public static <T> T get(Map<?, ?> map, String path) {
    String[] keys = path.split("\\.");
    for (int i = 0; i < keys.length; i++) {
      if (map.get(keys[i]) instanceof Map && keys.length > 1) {
        return MapUtil.<T>get((Map<?, ?>) map.get(keys[i]), path.substring(keys[i].length()+1));
      }
      @SuppressWarnings("unchecked")
      T result = (T) map.get(keys[i]);
      return result;
    }
    return null;
  }
}
