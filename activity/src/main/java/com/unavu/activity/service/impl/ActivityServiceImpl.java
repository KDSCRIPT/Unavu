package com.unavu.activity.service.impl;

import com.unavu.activity.repository.ActivityRepository;
import com.unavu.activity.service.ActivityService;
import com.unavu.activity.entity.Activity;
import com.unavu.activity.mapper.ActivityMapper;
import com.unavu.common.web.dto.ActivityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
    public void processActivityEvent(ActivityDto dto) {

        Activity activity = ActivityMapper.mapToActivity(dto);
        activityRepository.save(activity);
    }

    @Override
    public Page<Activity> getUserActivity(String userId, Pageable pageable) {
        return activityRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
