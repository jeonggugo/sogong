/**********************************************************
 공공데이터포털에 있는 오픈 API를 활용하여 기차시간표를 조회한다

 4번 API: 지역코드와 지역명
 3번 API: 역코드와 역명
 2번 API: 차량코드와 차량종류
 1번 API: 2,3,4번 API에서 습득한 정보를 바탕으로 시간표 조회
 ***********************************************************/

package sogong.train.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sogong.train.info.TrainInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TrainAPIController {
    @Value("${api.key}")
    private String apiKey;

    // API를 호출하여 모든 지역코드와 지역명을 가져온다
    // 데이터를 Map<Integer, String> 형식으로 반환
    @GetMapping("/trainAPI/4")
    @ResponseBody
    public Map<Integer, String> getCityCodes() {
        String URL4 = "http://apis.data.go.kr/1613000/" +
                "TrainInfoService/getCtyCodeList?serviceKey=%s&_type=json";
        Map<Integer, String> cityCodeMap = new HashMap<>();

        try {
            // API 서버에서 반환한 응답 데이터를 문자열로 저장
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(String.format(URL4, apiKey));
            String apiResult4 = restTemplate.getForObject(uri, String.class);

            // 문자열 형식의 JSON 데이터를 JsonNode 트리 구조로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResult4);

            // JSON 데이터에서 item 배열 노드를 가져온다
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                // 지역코드와 지역명을 찾아서 저장한다
                for (JsonNode item : itemsNode) {
                    Integer cityCode = item.path("citycode").asInt();
                    String cityName = item.path("cityname").asText();
                    cityCodeMap.put(cityCode, cityName);
                }
            }
            // 예외 처리
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
        return cityCodeMap;
    }

    // API를 호출하여 모든 역코드와 역명을 가져온다
    // 데이터를 Map<String, String> 형식으로 반환
    @GetMapping("/trainAPI/3")
    @ResponseBody
    public Map<String, String> getStationCodes() {
        String URL3 = "http://apis.data.go.kr/1613000/" +
                "TrainInfoService/getCtyAcctoTrainSttnList?" +
                "serviceKey=%s&_type=json&cityCode=%d";
        Map<String, String> stationCodeMap = new HashMap<>();
        Map<Integer, String> cityCodeMap = getCityCodes();

        try {
            // 각 지역 코드를 순회하면서 API 호출을 수행한다
            for (Integer cityCode : cityCodeMap.keySet()) {
                // API 서버에서 반환한 응답 데이터를 문자열로 저장
                RestTemplate restTemplate = new RestTemplate();
                URI uri = new URI(String.format(URL3, apiKey, cityCode));
                String apiResult3 = restTemplate.getForObject(uri, String.class);

                // 문자열 형식의 JSON 데이터를 JsonNode 트리 구조로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(apiResult3);

                // JSON 데이터에서 item 배열 노드를 가져온다
                JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

                if (itemsNode.isArray()) {
                    // 역코드와 역명을 찾아서 저장한다
                    for (JsonNode item : itemsNode) {
                        String nodeId = item.path("nodeid").asText();
                        String nodeName = item.path("nodename").asText();
                        stationCodeMap.put(nodeId, nodeName);
                    }
                }
            }

            // 예외처리
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
        return stationCodeMap;
    }

    // API를 호출하여 모든 차량코드와 차량종류 이름을 가져온다
    // 데이터를 Map<String, String> 형식으로 반환
    @GetMapping("/trainAPI/2")
    @ResponseBody
    public Map<String, String> getTrainGrades() {
        String URL2 = "http://apis.data.go.kr/1613000" +
                "/TrainInfoService/getVhcleKndList?serviceKey=%s&_type=json";
        Map<String, String> trainGradeMap = new HashMap<>();

        try {
            // API 서버에서 반환한 응답 데이터를 문자열로 저장
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(String.format(URL2, apiKey));
            String apiResult2 = restTemplate.getForObject(uri, String.class);

            // 문자열 형식의 JSON 데이터를 JsonNode 트리 구조로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResult2);

            // JSON 데이터에서 item 배열 노드를 가져온다
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                // 차량코드와 차량종류 이름을 찾아서 저장한다
                for (JsonNode item : itemsNode) {
                    String gradeCode = item.path("vehiclekndid").asText();
                    String gradeName = item.path("vehiclekndnm").asText();
                    trainGradeMap.put(gradeCode, gradeName);
                }
            }

            // 예외 처리
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
        return trainGradeMap;
    }

    // API를 호출하여 모든 역코드와 역명을 가져온다
    // 출발역 코드, 도착역 코드, 출발 날짜를 매개변수로 가진다
    // 데이터를 String 형식으로 반환
    @GetMapping("/trainAPI/1")
    @ResponseBody
    public String getTrainInfo(
            String depPlaceId,
            String arrPlaceId,
            String depPlandTime) {
        System.out.println("depPlaceId: " + depPlaceId);
        System.out.println("arrPlaceId: " + arrPlaceId);
        System.out.println("depPlandTime: " + depPlandTime);

        String URL1 = "http://apis.data.go.kr/1613000/" +
                "TrainInfoService/getStrtpntAlocFndTrainInfo?" +
                "serviceKey=%s&depPlaceId=%s&arrPlaceId=%s" +
                "&depPlandTime=%s&numOfRows=100&_type=json";
        List<TrainInfo> trainInfoList = new ArrayList<>();

        try {
            // API 서버에서 반환한 응답 데이터를 문자열로 저장
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(String.format(URL1, apiKey, depPlaceId, arrPlaceId, depPlandTime));
            String apiResult1 = restTemplate.getForObject(uri, String.class);
            System.out.println(uri);

            // 문자열 형식의 JSON 데이터를 JsonNode 트리 구조로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResult1);

            // JSON 데이터에서 item 배열 노드를 가져온다
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                // 출발시각, 도착시각, 차량종류를 찾아서 저장한다
                for (JsonNode item : itemsNode) {
                    String depTime = item.path("depplandtime").asText();
                    depTime = depTime.substring(8,12);
                    String arrTime = item.path("arrplandtime").asText();
                    arrTime = arrTime.substring(8,12);
                    String trainName = item.path("traingradename").asText();

                    TrainInfo trainInfo = new TrainInfo(arrTime, depTime, trainName);
                    trainInfoList.add(trainInfo);
                }
            } else {
                return "직통 열차 없음";
            }

            // 예외 처리
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
        return trainInfoList.toString();
    }

    // 열차 시간표를 가져오는 기능을 수행한다
    // 출발역, 도착열, 출발 날짜를 입력받는다
    // 데이터를 List<Map<String, String>> 형식으로 반환
    public List<Map<String, String>> getTrainSchedule(
            String depPlaceId,
            String arrPlaceId,
            String depPlandTime
    ) {
        String URL = "http://apis.data.go.kr/1613000/" +
                "TrainInfoService/getStrtpntAlocFndTrainInfo?" +
                "serviceKey=%s&depPlaceId=%s&arrPlaceId=%s" +
                "&depPlandTime=%s&numOfRows=100&_type=json";
        List<Map<String, String>> trainSchedule = new ArrayList<>();

        try {
            // API 서버에서 반환한 응답 데이터를 문자열로 저장
            RestTemplate restTemplate = new RestTemplate();

            URI uri = new URI(String.format(URL, apiKey, depPlaceId, arrPlaceId, depPlandTime));
            String apiResponse = restTemplate.getForObject(uri, String.class);

            // 문자열 형식의 JSON 데이터를 JsonNode 트리 구조로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);

            // JSON 데이터에서 item 배열 노드를 가져온다
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray()) {
                // 출발시각, 도착시각, 차량종류, 열차번호를 찾아서 저장한다.
                for (JsonNode item : itemsNode) {
                    String depTime = item.path("depplandtime").asText().substring(8, 12); // HHmm 추출
                    String arrTime = item.path("arrplandtime").asText().substring(8, 12); // HHmm 추출
                    String trainName = item.path("traingradename").asText();
                    String trainNo = item.path("trainno").asText();

                    // 위에서 저장한 데이터를 형식에 맞게 변형한다
                    Map<String, String> schedule = new HashMap<>();
                    schedule.put("depTime", depTime.substring(0, 2) + ":" + depTime.substring(2, 4)); // HH:mm 형식
                    schedule.put("arrTime", arrTime.substring(0, 2) + ":" + arrTime.substring(2, 4)); // HH:mm 형식
                    schedule.put("trainName", trainName);
                    schedule.put("trainNum", trainNo);

                    trainSchedule.add(schedule);
                }
            }

        // 예외 처리
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI 형식이 잘못되었습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("열차 시간표 조회 중 오류 발생", e);
        }
        return trainSchedule;
    }
}