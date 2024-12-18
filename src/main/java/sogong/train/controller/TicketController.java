package sogong.train.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            @RequestParam String passengerCount,
            @RequestParam String departureTime,
            @RequestParam String pkId,
            @RequestParam String selectSeat,
            Model model) {
        int charge = Integer.parseInt(adultCharge);
        int passenger = Integer.parseInt(passengerCount);
        int totlaCharge = charge * passenger;
        model.addAttribute("depTime", depTime);
        model.addAttribute("arrTime", arrTime);
        model.addAttribute("trainName", trainName);
        model.addAttribute("trainNum", trainNum);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("adultCharge", adultCharge);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("totalCharge", totlaCharge);
        model.addAttribute("departureTime", departureTime);
        model.addAttribute("pkId", pkId);
        model.addAttribute("selectSeat", selectSeat);
        return "payment";
    }



}


