package com.unavu.activity.service;

import com.unavu.activity.entity.Activity;
import com.unavu.common.web.dto.ActivityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityService {

    void processActivityEvent(ActivityDto dto);

    Page<Activity> getUserActivity(String userId, Pageable pageable);


}