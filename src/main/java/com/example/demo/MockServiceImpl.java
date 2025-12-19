package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class MockServiceImpl implements MockService {

	@Override
	public void something(final String id) {
		System.out.println(id);
	}

}
