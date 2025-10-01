package com.mixo.service;

import java.util.Map;

import com.mixo.model.Borrower;

public interface AllCloudApiService {

	Map<String, String> callAllCloudApi(Borrower borrowerObj);

}
