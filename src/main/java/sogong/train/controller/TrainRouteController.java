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
            @RequestParam String departure,       // 출발지
            @RequestParam String arrival,         // 도착지
            @RequestParam String departureTime,   // 출발 날짜 (YYYY-MM-DD)
            @RequestParam(defaultValue = "1") int passengerCount, // 승객 수
            Model model
    ) {
        // 날짜 포맷 변경 (Flatpickr에서 넘어온 YYYY-MM-DD → YYYYMMDD)
        String formattedDate = departureTime.replace("-", "");

        // TrainAPIController를 통해 열차 시간표 조회
        List<Map<String, String>> trainSchedule = trainAPIController.getTrainSchedule(
                departure,
                arrival,
                formattedDate
        );

        // 검색 결과를 모델에 추가하여 Thymeleaf 템플릿에서 사용 가능하도록 설정
        model.addAttribute("trainSchedule", trainSchedule);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("departureTime", departureTime);
        model.addAttribute("passengerCount", passengerCount);

        // 검색 결과 페이지로 이동
        return "search_results";
    }
}
