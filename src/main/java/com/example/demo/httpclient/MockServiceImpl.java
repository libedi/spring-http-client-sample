package com.example.demo.httpclient;

import org.springframework.stereotype.Service;

@Service
public class MockServiceImpl implements MockService {

	@Override
	public void something(final String id) {
		System.out.println(id);
	}

}
