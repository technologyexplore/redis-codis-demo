package com.itmuch.cloud.study.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.itmuch.cloud.study.entity.Grade;
import com.itmuch.cloud.study.mapper.GradeMapper;

@Service
public class GradeService {

	@Resource
	private GradeMapper gradeMapper;

	public List<Grade> getByGradeNm(String name) {
		return gradeMapper.getByGradeNm(name);
	}

}