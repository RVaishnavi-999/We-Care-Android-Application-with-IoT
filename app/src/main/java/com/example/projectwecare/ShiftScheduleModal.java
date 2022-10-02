package com.example.projectwecare;

public class ShiftScheduleModal {
    String startShiftDate, endShiftDate, shiftType, holiday, id, name;

    public ShiftScheduleModal() {

    }

    public ShiftScheduleModal(String startShiftDate, String endShiftDate, String shiftType, String holiday, String id, String name) {
        this.startShiftDate = startShiftDate;
        this.endShiftDate = endShiftDate;
        this.shiftType = shiftType;
        this.holiday = holiday;
        this.id = id;
        this.name = name;
    }

    public String getStartShiftDate() {
        return startShiftDate;
    }

    public void setStartShiftDate(String startShiftDate) {
        this.startShiftDate = startShiftDate;
    }

    public String getEndShiftDate() {
        return endShiftDate;
    }

    public void setEndShiftDate(String endShiftDate) {
        this.endShiftDate = endShiftDate;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
