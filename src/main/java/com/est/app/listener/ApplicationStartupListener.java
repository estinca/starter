package com.est.app.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.est.app.exception.RepositoryException;
import com.est.app.service.SetupService;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent>{
private final SetupService setupService;
	
	@Autowired
	public ApplicationStartupListener(SetupService setupService) {
		this.setupService = setupService;
	} 
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			if(!setupService.isRepositorySetup()) {
				setupService.setupRepository();
			}
		} catch(RepositoryException e) {
			e.printStackTrace();
			System.exit(9);
		}
	}
}
