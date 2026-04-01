package com.smarthome.iot.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.smarthome.iot.domain.Device;
import com.smarthome.iot.service.DeviceService;

@Controller
public class DeviceController {
    
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService){
        this.deviceService = deviceService;
    }

    @GetMapping("/admin/device")
    public String getDevicePage(Model model){
        List<Device> devices = this.deviceService.getAllDevice();
        model.addAttribute("devices", devices);
        return "admin/device/show";
    }


    @GetMapping("/admin/device/create")
    public String getDeviceCreatePage(Model model){
        model.addAttribute("newDevice", new Device());
        return "admin/device/create";
    }

    @PostMapping("/admin/device/create")
    public String postDeviceCreate(Model model, @ModelAttribute("newDevice") Device device){
        this.deviceService.createDevice(device);
        return "redirect:/admin/device";
    }
}
