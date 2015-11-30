package com.jlg.env.cli.common.service;

import com.jlg.env.cli.common.support.MapUtil;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class MapUtilTest {

  @Test
  public void should_return_expected_key() {
    //given
    Map<String, Object> map = of("droplet", of("id", "123"));

    //when
    String result = MapUtil.get(map, "droplet.id");

    //
    assertThat(result, equalTo("123"));

  }
}