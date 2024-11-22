/**********************************************************
 사용자가 각종 URL에 접속했을 때의 상황을 제어한다

 1. /train/reservation: 예약이 완료된 후의 화면이다
 2. /train/search: 예약하고자 하는 열차의 정보를 입력하는 화면이다
 3. /train/timetable: 시간표를 조회하여 예약할 수 있는 화면이다
 ***********************************************************/
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            ,ModelMap model) {
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
        Map<String, String> trainGrades = trainAPIController.getTrainGrades();

        model.addAttribute("stationCodes", stationCodes);
        model.addAttribute("trainGrades", trainGrades);
        return "train_search";
    }

    // 시간표를 조회하여 예약하는 화면이다
    // 출발역과 도착역 코드, 출발 날짜, 그리고 예약 인원 수를 입력받는다
    // 총 금액과 열차 정보를 train_timetable.html에 전달한다
    @GetMapping("/train/timetable")
    // 사용자가 폼에서 입력한 데이터를 받아 처리한다
    public String timetable(
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
        System.out.println("timeTable: " + trainInfo);

        if (trainInfo.equals("직통 열차 없음")) {
            model.addAttribute("message", "직통 열차 없음");
        }

        // 예약화면(/train/reservation)용으로 API정보를 다른 형식으로 가져온다
        List<Map<String, String>> trainSchedule = trainAPIController.getTrainSchedule(
                depPlaceId,
                arrPlaceId,
                formattedDepPlandTime
        );
        System.out.println("timeTable 2" + trainSchedule);

        // 비용 계산
        // 성인 = 23700
        // 어린이 = 50% 할인
        // 노인 = 30% 할인
        int fare = 23700;
        int finalFare = (int)((fare * Integer.parseInt(adultCount))
                + (fare * 0.5 * Integer.parseInt(childCount))
                + (fare * 0.7 * Integer.parseInt(seniorCount)));

        this.finalFare = Integer.toString(finalFare);

        model.addAttribute("fare", finalFare);               // 총 가격
        model.addAttribute("trainDetails", trainInfo);    // 열차 상세 정보
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