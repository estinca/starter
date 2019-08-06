package com.est.app.service;

import com.est.app.exception.RepositoryException;

public interface SetupService {

    boolean isRepositorySetup() throws RepositoryException;

    void setupRepository() throws RepositoryException;
}
