package dao.mapper;

import model.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student fromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setPassword(rs.getString("password"));
        student.setLastName(rs.getString("last_name"));
        student.setFirstName(rs.getString("first_name"));
        student.setMiddleName(rs.getString("middle_name"));
        student.setSuffix(rs.getString("suffix"));
        student.setBirthDate(rs.getString("birth_date"));
        student.setSex(rs.getString("sex"));
        student.setMobileNumber(rs.getString("mobile_number"));
        student.setEmail(rs.getString("email"));
        student.setHomeAddress(rs.getString("home_address"));
        student.setGuardianName(rs.getString("guardian_name"));
        student.setGuardianMobile(rs.getString("guardian_mobile"));
        student.setLastSchoolAttended(rs.getString("last_school_attended"));
        student.setShsStrand(rs.getString("shs_strand"));
        student.setCollege(rs.getString("college"));
        student.setProgram(rs.getString("program"));
        student.setYearLevel(rs.getInt("year_level"));
        student.setBlockSection(rs.getString("block_section"));
        return student;
    }
}
