package sogong.train.controller;

import org.springframework.web.bind.annotation.PostMapping;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class TrainController {

    private final TrainAPIController trainAPIController;

    // 사용자가 예약한 열차 정보 (전역 상태를 제거하고 지역 변수로 처리)
    private String date;
    private String departure;
    private String arrival;
    private String finalFare;

    public TrainController(TrainAPIController trainAPIController) {
        this.trainAPIController = trainAPIController;
    }

    /**
     * 예약 결과를 출력하는 화면
     */
    @GetMapping("/train/reservation")
    public String reservation(
            @RequestParam String result,
            ModelMap model) {

        System.out.println("Reservation Result: " + result);
        model.addAttribute("date", date);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("finalFare", finalFare);
        model.addAttribute("timeTable", result);
        return "train_reservation";
    }

    /**
     * 열차 정보 입력 화면
     */
    @GetMapping("/train/search")
    public String showTimetableForm(ModelMap model) {
        Map<String, String> stationCodes = trainAPIController.getStationCodes();
        Map<String, String> trainGrades = trainAPIController.getTrainGrades();

        model.addAttribute("stationCodes", stationCodes);
        model.addAttribute("trainGrades", trainGrades);
        return "search_result";
    }

    public String setStationCodes(String name){
        switch(name){
            case "서울":
                name = "NAT010000";
                break;
            case "수원":
                name = "NAT010415";
                break;
            case "평택":
                name = "NAT010754";
                break;
            case "대전":
                name = "NAT011668";
                break;
            case "동대구":
                name = "NAT013271";
                break;
            case "부산":
                name = "NAT014445";
                break;
            case "천안아산" :
                name ="NATH10960";
                break;
        }
        return name;
    }

    @PostMapping("/search")
    public String searchTrains(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String departureTime,
            @RequestParam(required = false, defaultValue = "1") String passengerCount,
            Model model
    ) {
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().toString(); // 기본값 설정 (오늘 날짜)
        }


        String depCode = setStationCodes(departure);
        String arrCode = setStationCodes(arrival);
        System.out.println("departure: " + departure);
        System.out.println("arrival: " + arrival);

        this.date = date;

        try {
            System.out.println("1 "+departureTime);
            String formattedDate = date.replace("-", "");
            System.out.println("2 " + formattedDate);


            // API를 사용하여 열차 시간표 가져오기
            List<Map<String, String>> trainSchedule = trainAPIController.getTrainSchedule(depCode, arrCode, formattedDate);

            // 모델에 필요한 데이터 추가
            model.addAttribute("departure", departure);
            model.addAttribute("arrival", arrival);
            model.addAttribute("departureTime", departureTime);
            model.addAttribute("passengerCount", passengerCount);

            if (trainSchedule == null || trainSchedule.isEmpty()) {
                model.addAttribute("message", "검색 결과가 없습니다.");
                model.addAttribute("trainSchedule", Collections.emptyList());
            } else {
                model.addAttribute("trainSchedule", trainSchedule);
            }

            return "search_results"; // 검색 결과를 보여줄 HTML 파일 이름

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "기차 시간표 검색 중 오류가 발생했습니다.");
            return "error"; // 오류 시 보여줄 HTML 파일 이름
        }
    }

    /**
     * 시간표 조회 및 예약 화면
     */
    @GetMapping("/train/timetable")
    public String timetable(
            @RequestParam String depPlaceId,
            @RequestParam String arrPlaceId,
            @RequestParam String depPlandTime,
            @RequestParam(required = false, defaultValue = "1") String adultCount,
            @RequestParam(required = false, defaultValue = "0") String childCount,
            @RequestParam(required = false, defaultValue = "0") String seniorCount,
            Model model
    ) {
        try {
            // 출발지 및 도착지 이름 조회
            Map<String, String> stationCodes = trainAPIController.getStationCodes();
            String departure = stationCodes.getOrDefault(depPlaceId, "출발지");
            String arrival = stationCodes.getOrDefault(arrPlaceId, "목적지");

            this.date = depPlandTime;
            this.departure = departure;
            this.arrival = arrival;

            model.addAttribute("departure", departure);
            model.addAttribute("arrival", arrival);

            // 날짜 포맷 변경 (YYYY-MM-DD → YYYYMMDD)
            String formattedDepPlandTime = LocalDate.parse(depPlandTime).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // API 호출로 열차 정보 조회
            String trainInfo = trainAPIController.getTrainInfo(depPlaceId, arrPlaceId, formattedDepPlandTime);
            System.out.println("Train Info: " + trainInfo);

            if ("직통 열차 없음".equals(trainInfo)) {
                model.addAttribute("message", "직통 열차 없음");
                return "train_timetable";
            }

            // API 호출로 열차 시간표 가져오기
            List<Map<String, String>> trainSchedule = trainAPIController.getTrainSchedule(depPlaceId, arrPlaceId, formattedDepPlandTime);
            System.out.println("Train Schedule: " + trainSchedule);

            // 비용 계산
            int fare = 23700;
            int finalFare = calculateFare(fare, adultCount, childCount, seniorCount);
            this.finalFare = String.valueOf(finalFare);

            model.addAttribute("fare", finalFare);
            model.addAttribute("trainDetails", trainInfo);
            model.addAttribute("trainSchedule", trainSchedule);

            return "train_timetable";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "시간표 조회 중 오류가 발생했습니다.");
            return "error";
        }
    }

    /**
     * 비용 계산
     */
    private int calculateFare(int baseFare, String adultCount, String childCount, String seniorCount) {
        try {
            return (int) ((baseFare * Integer.parseInt(adultCount))
                    + (baseFare * 0.5 * Integer.parseInt(childCount))
                    + (baseFare * 0.7 * Integer.parseInt(seniorCount)));
        } catch (NumberFormatException e) {
            System.err.println("Invalid passenger count input.");
            return 0;
        }
    }

    /**
     * 문자열 데이터를 객체로 변환
     */
    private List<TrainInfo> parseTrainInfo(String trainInfoString) {
        List<TrainInfo> trainInfoList = new ArrayList<>();

        if (trainInfoString == null || trainInfoString.isEmpty()) {
            System.err.println("API 응답이 비어 있습니다.");
            return trainInfoList;
        }

        try {
            String[] trainEntries = trainInfoString.split("TrainInfo");

            for (String entry : trainEntries) {
                if (entry.contains("arrTime") && entry.contains("depTime") && entry.contains("trainNum") && entry.contains("adultcharge")) {
                    String arrTime = extractValue(entry, "arrTime='");
                    String depTime = extractValue(entry, "depTime='");
                    String trainNum = extractValue(entry, "trainNum='");
                    String adultcharge = extractValue(entry, "adultcharge='");

                    if (!arrTime.isEmpty() && !depTime.isEmpty() && !trainNum.isEmpty() && !adultcharge.isEmpty()) {
                        trainInfoList.add(new TrainInfo(arrTime, depTime, trainNum, adultcharge));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing train info: " + e.getMessage());
            e.printStackTrace();
        }

        return trainInfoList;
    }

    /**
     * 문자열에서 키 값 추출
     */
    private String extractValue(String data, String key) {
        String[] splitData = data.split(key);
        return splitData.length > 1 ? splitData[1].split("'")[0] : "";
    }
}
