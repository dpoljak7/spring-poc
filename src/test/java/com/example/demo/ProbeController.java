package com.example.demo;

import com.example.api.controller.ProbeApi;
import com.example.api.model.ProbeAuditGet200ResponseInner;
import com.example.api.model.ProbeCommandPostRequest;
import com.example.api.model.ProbeInitPostRequest;
import com.example.api.model.ProbeState;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbeController implements ProbeApi {

  @Override
  public ResponseEntity<List<ProbeAuditGet200ResponseInner>> probeAuditGet() {
    return ProbeApi.super.probeAuditGet();
  }

  @Override
  public ResponseEntity<ProbeState> probeCommandPost(
      ProbeCommandPostRequest probeCommandPostRequest) {
    return ProbeApi.super.probeCommandPost(probeCommandPostRequest);
  }

  @Override
  public ResponseEntity<Void> probeInitPost(ProbeInitPostRequest probeInitPostRequest) {
    return ProbeApi.super.probeInitPost(probeInitPostRequest);
  }

  @Override
  public ResponseEntity<ProbeState> probeStatusGet() {
    return ProbeApi.super.probeStatusGet();
  }
}
