package sogong.train.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sogong.train.api.TrainAPIController;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TrainRouteController {

    // TrainAPIController 주입
    private final TrainAPIController trainAPIController;

    /**
     * 열차 경로 검색 메서드
     * 사용자로부터 출발지, 도착지, 날짜 등을 입력받아 열차 정보를 검색
     */
    @PostMapping("/search")
    public String searchTrains(
            @RequestParam String departure,       // 출발지 (예: 서울)
            @RequestParam String arrival,         // 도착지 (예: 부산)
            @RequestParam String departureTime,   // 출발 날짜 (YYYY-MM-DD 형식)
            @RequestParam(defaultValue = "KTX") String trainGrade, // 열차 종류 (예: KTX, ITX)
            @RequestParam(defaultValue = "1") int passengerCount,  // 승객 수
            Model model
    ) {
        // 날짜 포맷 변경 (YYYY-MM-DD → YYYYMMDD)
        String formattedDate = departureTime.replace("-", "");

        // 출발지 및 도착지의 역 코드 조회
        Map<String, String> stationCodes = trainAPIController.getStationCodes();
        String depPlaceId = stationCodes.getOrDefault(departure, null);
        String arrPlaceId = stationCodes.getOrDefault(arrival, null);

      /*  if (depPlaceId == null || arrPlaceId == null) {
            model.addAttribute("error", "출발지 또는 도착지가 올바르지 않습니다.");
            return "search_error"; // 에러 페이지 반환
        }*/

        // TrainAPIController를 통해 열차 시간표 조회
        List<Map<String, String>> trainSchedule = trainAPIController.getTrainSchedule(
                depPlaceId,
                arrPlaceId,
                formattedDate,
                trainGrade
        );

        // 검색 결과를 모델에 추가하여 Thymeleaf 템플릿에서 사용 가능하도록 설정
        model.addAttribute("trainSchedule", trainSchedule);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("departureTime", departureTime);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("trainGrade", trainGrade);

        // 검색 결과 페이지로 이동
        return "search_results";
    }
}
