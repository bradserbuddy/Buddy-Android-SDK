package com.buddy.sdk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class JsonSerializerTest {

    private static final String RECIPIENT_UID = "blj.hHdbbzkmhjFC";

    @Test
    public void testSerializeIdList(){
        final List<String> r = Lists.newArrayList(RECIPIENT_UID);
        final Gson s = BuddyServiceClientImpl.makeRequestSerializer();
        String jsonlist = s.toJson(r);
        List<String> rt = (List<String>) s.fromJson(jsonlist, List.class);
        assertTrue(rt.get(0).equals(RECIPIENT_UID));
        Map<String,Object> requestParams = Maps.newHashMap();
        requestParams.put("recipients", r);
        requestParams.put("title", "Shit just goat real");
        jsonlist = s.toJson(requestParams);


    }


}
