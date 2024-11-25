package sogong.train.controller;


import jakarta.persistence.Id;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sogong.train.dto.MemberDTO;
import sogong.train.service.MemberService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    //생성자 주입
    private final MemberService memberService;

    @GetMapping("/sogong/train/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/sogong/train/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("sogong save");
        System.out.println("username: " + memberDTO);
        memberService.save(memberDTO);
        return "redirect:/sogong/train/login";
    }
    @GetMapping("/sogong/train/login")
    public String loginForm() {
        return "login";
    }
    @PostMapping("/sogong/train/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
       MemberDTO loginResult = memberService.login(memberDTO);
       if (loginResult != null) {
           // login성공
           session.setAttribute("Id", loginResult.getId());
           session.setAttribute("loginEmail", loginResult.getEmail());
           session.setAttribute("loginName", loginResult.getName());
           session.setAttribute("role", loginResult.getRole());
           return "redirect:/sogong/train/main";
       }
       else{
           return "login";
       }
    }
    @GetMapping("/sogong/train/main")//그인 되지 않은 유저가 접속 시도 시 로그인 페이지로 이동
    public String mainForm(HttpSession session, Model model) {
        String loginName = (String) session.getAttribute("loginName");
        if (loginName != null) {
            model.addAttribute("loginName", loginName);
            return "main";
        } else {
            return "redirect:/sogong/train/login";
        }
    }
    @GetMapping("/sogong/train/admin")
    public String adminForm() {
        return "admin";
    }
    @GetMapping("/sogong/train/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    @GetMapping("/sogong/train/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        memberService.DeleteById(id);
        return "redirect:/sogong/train/";
    }
    @GetMapping("/sogong/train/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }
}
