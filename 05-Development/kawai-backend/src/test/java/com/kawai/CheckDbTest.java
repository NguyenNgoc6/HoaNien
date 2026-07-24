package com.kawai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.kawai.repositories.StaffScheduleRepository;
import com.kawai.models.StaffSchedule;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class CheckDbTest {

    @Autowired
    private StaffScheduleRepository staffScheduleRepository;

    @Test
    @org.springframework.transaction.annotation.Transactional
    public void testCount() {
        LocalDate today = LocalDate.now();
        System.out.println("TODAY_DATE: " + today);
        List<StaffSchedule> schedules = staffScheduleRepository.findByWorkDateAndStatus(today, "Published");
        System.out.println("TOTAL_SHIFTS: " + schedules.size());
        for (StaffSchedule s : schedules) {
            if (s.getEmployee() != null && s.getEmployee().getAccount() != null && s.getEmployee().getAccount().getRole() != null) {
                String roleName = s.getEmployee().getAccount().getRole().getRoleName();
                if ("MAINTAINER".equalsIgnoreCase(roleName)) {
                    System.out.println("MAINTAINER_SHIFT: Username=" + s.getEmployee().getAccount().getUsername() 
                        + ", Name=" + s.getEmployee().getFullName() 
                        + ", Shift=" + s.getShift().getShiftName() 
                        + " (" + s.getShift().getStartTime() + " - " + s.getShift().getEndTime() + ")");
                }
            }
        }
    }
}
