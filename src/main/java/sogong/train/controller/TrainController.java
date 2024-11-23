package sogong.train.controller;

import sogong.train.api.TrainAPIController;
import sogong.train.info.TrainInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class TrainController {
    // 사용자가 예약한 열차의 정보를 저장한다
    private String date;
    private String departure;
    private String arrival;
    private String finalFare;
    private String timeTable;

    private final TrainAPIController trainAPIController;

    public TrainController(TrainAPIController trainAPIController) {
        this.trainAPIController = trainAPIController;
    }

    // 예약 결과를 출력하는 화면이다
    // result(열차 시간표 정보)를 입력받는다
    // 전달받은 데이터를 train_reservation.html에 전달한다
    @GetMapping("/train/reservation")
    public String reservation(
            @RequestParam String result
            , ModelMap model) {
        timeTable = result;
        System.out.println("timeTable: " + timeTable);

        model.addAttribute("date", date);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("finalFare", finalFare);
        model.addAttribute("timeTable", timeTable);
        return "train_reservation";
    }

    // 열차 정보 입력을 위한 화면이다
    // 역 코드와 차량 정보를 train_search.html에 전달한다
    @GetMapping("/train/search")
    public String showTimetableForm(ModelMap model) {
        Map<String, String> stationCodes = trainAPIController.getStationCodes();
        String[] trainGrades = {"ITX"};

        // 역이름 가나다순으로 정렬
        Map<String, String> sortedStationCodes = new HashMap<>();
        List<Map.Entry<String, String>> entryList = new LinkedList<>(stationCodes.entrySet());
        entryList.sort(Map.Entry.comparingByValue());
        for (Map.Entry<String, String> entry : entryList) {
            System.out.println("key : " + entry.getKey() + ", value : " + entry.getValue());
        }

        model.addAttribute("trainGrades", trainGrades);
        model.addAttribute("stationCodes", entryList);
        return "train_search";
    }

    // 시간표를 조회하여 예약하는 화면이다
    // 출발역과 도착역 코드, 출발 날짜, 그리고 예약 인원 수를 입력받는다
    // 총 금액과 열차 정보를 train_timetable.html에 전달한다
    @GetMapping("/train/timetable")
    // 사용자가 폼에서 입력한 데이터를 받아 처리한다
    public String timetable(
            @RequestParam String gradeId,           // 열차 종류
            @RequestParam String depPlaceId,        // 출발역 코드
            @RequestParam String arrPlaceId,        // 도착역 코드
            @RequestParam String depPlandTime,      // 출발 날짜 (YYYY-MM-DD)

            @RequestParam(required = false, defaultValue = "1") String adultCount,  // 어른 수
            @RequestParam(required = false, defaultValue = "0") String childCount,  // 어린이 수
            @RequestParam(required = false, defaultValue = "0") String seniorCount, // 경로 수
            Model model
    ) {
        Map<String, String> stationCodes = trainAPIController.getStationCodes();
        String departure = stationCodes.getOrDefault(depPlaceId, "출발지");
        String arrival = stationCodes.getOrDefault(arrPlaceId, "목적지");

        this.departure = departure;
        this.arrival = arrival;
        this.date = depPlandTime;

        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);

        // 입력받은 날짜를 api 링크 형식에 맞게 바꾼다
        String formattedDepPlandTime = LocalDate.parse(depPlandTime).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // API를 호출하여 열차 정보를 가져온다
        String trainInfo = trainAPIController.getTrainInfo(
                depPlaceId,
                arrPlaceId,
                formattedDepPlandTime
        );

        if (trainInfo.equals("직통 열차 없음")) {
            model.addAttribute("message", "직통 열차 없음");
        }

        // 비용 계산
        // 성인 = 23,700원
        // 어린이 = 50% 할인
        // 노인 = 30% 할인
        int fare = 23700;
        int finalFare = (int) ((fare * Integer.parseInt(adultCount))
                + (fare * 0.5 * Integer.parseInt(childCount))
                + (fare * 0.7 * Integer.parseInt(seniorCount)));

        this.finalFare = Integer.toString(finalFare);

        model.addAttribute("fare", finalFare);               // 총 가격
        model.addAttribute("trainDetails", trainInfo);    // 열차 상세 정보


        // 예약화면(/train/reservation)용으로 API정보를 다른 형식으로 가져온다
        List<Map<String, String>> trainSchedule = trainAPIController.getTrainSchedule(
                depPlaceId,
                arrPlaceId,
                formattedDepPlandTime,
                gradeId
        );
        System.out.println("timeTable 2" + trainSchedule);

        model.addAttribute("trainSchedule", trainSchedule);

        return "train_timetable";
    }

    // 문자열 데이터를 객체로 변환한다
    // TrainInfo 데이터를 입력받는다
    // 데이터를 List<TrainInfo> 형식으로 반환한다
    private List<TrainInfo> parseTrainInfo(String trainInfoString) {
        List<TrainInfo> trainInfoList = new ArrayList<>();

        if (trainInfoString == null || trainInfoString.isEmpty()) {
            System.err.println("API 응답이 비어 있습니다.");
            return trainInfoList;
        }

        String[] trainEntries = trainInfoString.split("TrainInfo");

        for (String entry : trainEntries) {
            try {
                // 문자열을 분리하여 arrTime, depTime, trainNum 값을 추출한다
                if (entry.contains("arrTime") && entry.contains("depTime") && entry.contains("trainNum")) {
                    String[] arrTimeSplit = entry.split("arrTime='");
                    String[] depTimeSplit = entry.split("depTime='");
                    String[] trainNumSplit = entry.split("trainNum='");

                    String arrTime = arrTimeSplit.length > 1 ? arrTimeSplit[1].split("'")[0] : "";
                    String depTime = depTimeSplit.length > 1 ? depTimeSplit[1].split("'")[0] : "";
                    String trainNum = trainNumSplit.length > 1 ? trainNumSplit[1].split("'")[0] : "";

                    if (!arrTime.isEmpty() && !depTime.isEmpty() && !trainNum.isEmpty()) {
                        trainInfoList.add(new TrainInfo(arrTime, depTime, trainNum));
                    }
                }

                // 예외 처리
            } catch (Exception e) {
                System.err.println("Error parsing entry: " + entry);
                e.printStackTrace();
            }
        }
        return trainInfoList;
    }
}