package com.itmuch.cloud.study.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.itmuch.cloud.study.entity.Grade;

public interface GradeMapper {

//	@Select("select * from grade where grade_nm=#{name}")
	@Select("select * from grade")
	public List<Grade> getByGradeNm(String name);

	@Insert("insert into grade(grade_nm,teacher_id) values(#{gradeNm},#{teacherId})")
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id") // 设置id自增长
	public void save(Grade grade);
}
