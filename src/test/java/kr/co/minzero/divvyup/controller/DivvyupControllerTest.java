package kr.co.minzero.divvyup.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.minzero.divvyup.constants.ResponseCode;
import kr.co.minzero.divvyup.common.DivvyupResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DivvyupControllerTest {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(DivvyupControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    public void testDivvyup() throws Exception {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("money", "100000");
        params.put("count", "3");
        String content = objectMapper.writeValueAsString(params);

        // when
        final ResultActions actions = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", "123456")
                .header("X-ROOM-ID", "ROOM001")
                .content(content));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());
    }

    @Test
    public void testTakeMoney() throws Exception {

        // given
        String senderId = "123456";
        String receiverId = "222222";
        String roomId = "ROOM001";

        Map<String, String> params = new HashMap<>();
        params.put("money", "1000000");
        params.put("count", "32");

        MvcResult result = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", senderId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        // get token
        DivvyupResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), DivvyupResponse.class);
        Map<String, String> resultMap = (Map<String, String>) res.getBody();

        String token = resultMap.get("token");

        // when
        final ResultActions actions = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId)
                .header("X-ROOM-ID", roomId));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());
    }

    @Test
    public void testTakeMoneyFail() throws Exception {

        // given
        String senderId = "222222";
        String receiverId = "333333";
        String roomId = "ROOM002";

        Map<String, String> params = new HashMap<>();
        params.put("money", "1000000");
        params.put("count", "32");

        MvcResult result = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", senderId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        // get token
        DivvyupResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), DivvyupResponse.class);
        Map<String, String> resultMap = (Map<String, String>) res.getBody();

        String token = resultMap.get("token");

        // when
        final ResultActions actions = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId)
                .header("X-ROOM-ID", roomId));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.FAIL)))
                .andDo(print());
    }


    @Test
    public void testTakeMoneyFail2() throws Exception {

        // given
        String senderId = "123456";
        String receiverId = "222222";
        String roomId = "ROOM001";

        Map<String, String> params = new HashMap<>();
        params.put("money", "1000000");
        params.put("count", "3");

        MvcResult result = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", senderId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        // get token
        DivvyupResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), DivvyupResponse.class);
        Map<String, String> resultMap = (Map<String, String>) res.getBody();

        String token = resultMap.get("token");

        // when
        final ResultActions actions = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId)
                .header("X-ROOM-ID", roomId));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());

        // 두번 받기
        final ResultActions actions2 = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId)
                .header("X-ROOM-ID", roomId));

        // then
        actions2
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.FAIL)))
                .andDo(print());
    }

    @Test
    public void testTakeMoneyFail3() throws Exception {

        // given
        String senderId = "123456";
        String receiverId1 = "222222";
        String receiverId2 = "333333";
        String roomId = "ROOM001";

        Map<String, String> params = new HashMap<>();
        params.put("money", "1000000");
        params.put("count", "1");

        MvcResult result = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", senderId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        // get token
        DivvyupResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), DivvyupResponse.class);
        Map<String, String> resultMap = (Map<String, String>) res.getBody();

        String token = resultMap.get("token");

        // when
        final ResultActions actions = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId1)
                .header("X-ROOM-ID", roomId));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());

        // 전부 다 받은 경우
        final ResultActions actions2 = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId2)
                .header("X-ROOM-ID", roomId));

        // then
        actions2
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.FAIL)))
                .andDo(print());
    }

    @Test
    public void testViewResult() throws Exception {

        String userId = "123456";
        String roomId = "ROOM001";

        Map<String, String> params = new HashMap<>();
        params.put("money", "100000");
        params.put("count", "4");

        MvcResult result = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        DivvyupResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), DivvyupResponse.class);
        Map<String, String> resultMap = (Map<String, String>) res.getBody();

        String token = resultMap.get("token");

        mockMvc.perform(get("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());
    }

    @Test
    public void testViewResult2() throws Exception {

        String userId = "123456";
        String receiverId = "222222";
        String roomId = "ROOM001";

        Map<String, String> params = new HashMap<>();
        params.put("money", "100000");
        params.put("count", "2");

        MvcResult result = mockMvc.perform(post("/api/divvyup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        DivvyupResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), DivvyupResponse.class);
        Map<String, String> resultMap = (Map<String, String>) res.getBody();

        String token = resultMap.get("token");

        // when
        final ResultActions actions = mockMvc.perform(put("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", receiverId)
                .header("X-ROOM-ID", roomId));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());

        mockMvc.perform(get("/api/divvyup/" + token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS)))
                .andDo(print());
    }
}
