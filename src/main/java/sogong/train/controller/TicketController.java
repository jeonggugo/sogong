package sogong.train.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequiredArgsConstructor
public class TicketController {
    @PostMapping("/payment")
    public String reservation(
            @RequestParam String depTime,
            @RequestParam String arrTime,
            @RequestParam String trainName,
            @RequestParam String trainNum,
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String adultCharge,
            @RequestParam String pkId,
            Model model) {

        model.addAttribute("depTime", depTime);
        model.addAttribute("arrTime", arrTime);
        model.addAttribute("trainName", trainName);
        model.addAttribute("trainNum", trainNum);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("adultCharge", adultCharge);
        model.addAttribute("pkId", pkId);
        return "payment";
    }


}
