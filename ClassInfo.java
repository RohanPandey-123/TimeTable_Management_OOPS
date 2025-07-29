import java.util.List;

class ClassInfo {
    String code, instructor, days, roomNo, timeSlot;

    public ClassInfo(String code, String instructor, String days, String roomNo, String timeSlot) {
        this.code = code;
        this.instructor = instructor;
        this.days = days;
        this.roomNo = roomNo;
        this.timeSlot = timeSlot;
    }
    
    // Constructor used primarily for TeacherDashboard
    public ClassInfo(String code, String instructor, List<String> daysList, String timeSlot) {
        this.code = code;
        this.instructor = instructor;
        this.days = String.join(" ", daysList);
        this.roomNo = ""; // Default empty room
        this.timeSlot = timeSlot;
    }
    
    // Getter methods used by TeacherDashboard
    public String getCourse() {
        return code;
    }
    
    public String[] getDays() {
        return days.split(" ");
    }
    
    public String getTimeSlot() {
        return timeSlot;
    }
}