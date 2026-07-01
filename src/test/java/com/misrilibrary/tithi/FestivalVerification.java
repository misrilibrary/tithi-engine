package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import java.time.LocalDate;
import java.util.*;

/** Standalone verification — run with: javac + java (no JUnit needed) */
public class FestivalVerification {
    public static void main(String[] args) {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
        var truth = Map.ofEntries(
            Map.entry("maha_shivaratri_2025", "2025-02-26"),
            Map.entry("maha_shivaratri_2026", "2026-02-15"),
            Map.entry("holika_dahan_2025", "2025-03-13"),
            Map.entry("holika_dahan_2026", "2026-03-02"),
            Map.entry("ram_navami_2025", "2025-04-06"),
            Map.entry("ram_navami_2026", "2026-03-26"),
            Map.entry("akshaya_tritiya_2025", "2025-04-30"),
            Map.entry("akshaya_tritiya_2026", "2026-04-19"),
            Map.entry("guru_purnima_2025", "2025-07-10"),
            Map.entry("guru_purnima_2026", "2026-07-29"),
            Map.entry("janmashtami_smarta_2025", "2025-08-15"),
            Map.entry("janmashtami_smarta_2026", "2026-09-04"),
            Map.entry("diwali_2025", "2025-10-20"),
            Map.entry("diwali_2026", "2026-11-08")
        );

        int pass = 0, fail = 0;
        for (Festival fest : Festival.all()) {
            for (int year : new int[]{2025, 2026}) {
                String key = fest.id + "_" + year;
                String expected = truth.get(key);
                if (expected == null) continue;
                FestivalDate gotFd = panchang.dateFor(fest, year, City.of("Ujjain"));
                LocalDate got = gotFd == null ? null : gotFd.getDate();
                String gotStr = got != null ? got.toString() : "null";
                if (gotStr.equals(expected)) {
                    pass++;
                    System.out.println("✅ " + fest.name + " " + year + " = " + gotStr);
                } else {
                    fail++;
                    System.out.println("❌ " + fest.name + " " + year + " = " + gotStr + " (want " + expected + ")");
                }
            }
        }
        System.out.println("\n" + pass + " passed, " + fail + " failed");
    }
}
