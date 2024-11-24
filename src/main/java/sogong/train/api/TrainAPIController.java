package sogong.train.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import sogong.train.info.TrainInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;

@Slf4j
@Controller
public class TrainAPIController {

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 지역 코드 조회
    @GetMapping("/trainAPI/4")
    @ResponseBody
    public Map<Integer, String> getCityCodes() {
        String URL = "http://apis.data.go.kr/1613000/TrainInfoService/getCtyCodeList?serviceKey=%s&numOfRows=100&_type=json";
        Map<Integer, String> cityCodeMap = new HashMap<>();

        try {
            URI uri = new URI(String.format(URL, apiKey));
            String apiResponse = restTemplate.getForObject(uri, String.class);
            log.info("City Code API Response: {}", apiResponse);

            JsonNode rootNode = objectMapper.readTree(apiResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    Integer cityCode = item.path("citycode").asInt();
                    String cityName = item.path("cityname").asText();
                    cityCodeMap.put(cityCode, cityName);
                }
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP Error while calling City Code API: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error while parsing City Code API response", e);
        }

        return cityCodeMap;
    }

    // 역 코드 조회
    @GetMapping("/trainAPI/3")
    @ResponseBody
    public Map<String, String> getStationCodes() {
        String URL = "http://apis.data.go.kr/1613000/TrainInfoService/getCtyAcctoTrainSttnList?serviceKey=%s&numOfRows=100&_type=json&cityCode=%d";
        Map<String, String> stationCodeMap = new HashMap<>();
        Map<Integer, String> cityCodeMap = getCityCodes();

        try {
            for (Integer cityCode : cityCodeMap.keySet()) {
                URI uri = new URI(String.format(URL, apiKey, cityCode));
                String apiResponse = restTemplate.getForObject(uri, String.class);
                log.info("Station Code API Response for City Code {}: {}", cityCode, apiResponse);

                JsonNode rootNode = objectMapper.readTree(apiResponse);
                JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

                if (itemsNode.isArray()) {
                    for (JsonNode item : itemsNode) {
                        String nodeId = item.path("nodeid").asText();
                        String nodeName = item.path("nodename").asText();
                        stationCodeMap.put(nodeId, nodeName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while parsing Station Code API response", e);
        }

        return stationCodeMap;
    }

    // 차량 코드 조회
    @GetMapping("/trainAPI/2")
    @ResponseBody
    public Map<String, String> getTrainGrades() {
        String URL = "http://apis.data.go.kr/1613000/TrainInfoService/getVhcleKndList?serviceKey=%s&numOfRows=100&_type=json";
        Map<String, String> trainGradeMap = new HashMap<>();

        try {
            URI uri = new URI(String.format(URL, apiKey));
            String apiResponse = restTemplate.getForObject(uri, String.class);
            log.info("Train Grade API Response: {}", apiResponse);

            JsonNode rootNode = objectMapper.readTree(apiResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String gradeCode = item.path("vehiclekndid").asText();
                    String gradeName = item.path("vehiclekndnm").asText();
                    trainGradeMap.put(gradeCode, gradeName);
                }
            }
        } catch (Exception e) {
            log.error("Error while parsing Train Grade API response", e);
        }

        return trainGradeMap;
    }

    // 열차 정보 조회
    @GetMapping("/trainAPI/1")
    @ResponseBody
    public String getTrainInfo(String depPlaceId, String arrPlaceId, String depPlandTime) {
        String URL = "http://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo?serviceKey=%s&depPlaceId=%s&arrPlaceId=%s&depPlandTime=%s&numOfRows=100&_type=json";

        try {
            // API 키 인코딩
            String encodedApiKey = URLEncoder.encode(apiKey, "UTF-8");

            // URI 생성
            URI uri = new URI(String.format(URL, encodedApiKey, depPlaceId, arrPlaceId, depPlandTime));
            String apiResponse = restTemplate.getForObject(uri, String.class);
            log.info("Train Info API Response: {}", apiResponse);

            // JSON 형식이 아닌 경우 XML 오류 메시지를 처리
            if (apiResponse.startsWith("<")) {
                log.error("Invalid response received: {}", apiResponse);
                return "Error: Invalid API response. Please check the service key or try again later.";
            }

            // JSON 형식인지 검증
            if (!isValidJson(apiResponse)) {
                log.error("Invalid JSON response received: {}", apiResponse);
                return "Error: Invalid API response. Please check the service key or try again later.";
            }

            JsonNode rootNode = objectMapper.readTree(apiResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray() && itemsNode.size() > 0) {
                return itemsNode.toString();
            } else {
                return "직통 열차 없음";
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP Error while calling Train Info API: {}", e.getMessage());
            return "Error: API Call failed. Please check your API key.";
        } catch (Exception e) {
            log.error("Error while parsing Train Info API response", e);
            return "Error: Parsing API response failed. Please try again later.";
        }
    }


    // 시간표 조회
    public List<Map<String, String>> getTrainSchedule(String depPlaceId, String arrPlaceId, String depPlandTime) {
        List<Map<String, String>> trainSchedule = new ArrayList<>();
        String URL = "http://apis.data.go.kr/1613000/" +
                "TrainInfoService/getStrtpntAlocFndTrainInfo?" +
                "serviceKey=%s&depPlaceId=%s&arrPlaceId=%s" +
                "&depPlandTime=%s&trainGradeCode=%s&numOfRows=100&_type=json";

        try {
            String[] trainCodeArr = new String[]{"00", "16", "07", "19", "10"};

            for (String actualTrainCode : trainCodeArr) {
                URI uri = new URI(String.format(URL, apiKey, depPlaceId, arrPlaceId, depPlandTime, actualTrainCode));
                System.out.println("Request URI: " + uri);
                System.out.println("gradeCode: " + actualTrainCode);

                String apiResponse = restTemplate.getForObject(uri, String.class);

                // JSON 데이터를 처리하여 trainSchedule에 추가
                trainSchedule.addAll(parseTrainSchedule(apiResponse));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI 형식이 잘못되었습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("열차 시간표 조회 중 오류 발생", e);
        }
        return trainSchedule;
    }

    // 사용자가 입력한 열차 종류에 따라 서로 다른 api문서를 호출한다
    // 완성된 api 링크를 입력받는다
    // 데이터를 List<Map<String, String>> 형식으로 반환
    private List<Map<String, String>> parseTrainSchedule(String apiResponse) throws Exception {
        List<Map<String, String>> trainSchedule = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(apiResponse);

        JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                String depTime = item.path("depplandtime").asText().substring(8, 12); // HHmm 추출
                String arrTime = item.path("arrplandtime").asText().substring(8, 12); // HHmm 추출
                String trainName = item.path("traingradename").asText();
                String trainNo = item.path("trainno").asText();

                Map<String, String> schedule = new HashMap<>();
                schedule.put("depTime", depTime.substring(0, 2) + ":" + depTime.substring(2, 4)); // HH:mm 형식
                schedule.put("arrTime", arrTime.substring(0, 2) + ":" + arrTime.substring(2, 4)); // HH:mm 형식
                schedule.put("trainName", trainName);
                schedule.put("trainNum", trainNo);

                trainSchedule.add(schedule);
            }
        }
        return trainSchedule;
    }

    private boolean isValidJson(String response) {
        try {
            objectMapper.readTree(response);
            return true;
        } catch (Exception e) {
            log.error("Invalid JSON: {}", e.getMessage());
            return false;
        }
    }

}
