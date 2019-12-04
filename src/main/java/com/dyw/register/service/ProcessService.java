package com.dyw.register.service;

import com.dyw.register.dao.ProcessDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessService {
    @Autowired
    private ProcessDao processDao;

    public void setProcessId(int id) {
        processDao.setProcessId(id);
    }

    public void setRegisterStatus() {
        processDao.setRegisterStatus();
    }
}
