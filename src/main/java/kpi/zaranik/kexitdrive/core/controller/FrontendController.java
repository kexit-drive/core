package kpi.zaranik.kexitdrive.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("picker")
    public String pickerPage() {
        return "index";
    }

}
