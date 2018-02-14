package com.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.DocuSignEnvelopeInformation;

/**
 * Created by e746824 on 14/02/2018.
 */
@Controller
@RequestMapping("connect")
public class WebHookController {

  private final Map<String, BlockingQueue<DocuSignEnvelopeInformation>> eventsByEnvelopeId = new HashMap<>();

  @PostMapping(consumes = "application/xml")
  public ResponseEntity receiveEvent(@RequestBody DocuSignEnvelopeInformation event) {
    String envelopeId = event.getEnvelopeStatus().getEnvelopeID();
    if(!eventsByEnvelopeId.containsKey(envelopeId)) {
      eventsByEnvelopeId.put(envelopeId, new LinkedBlockingDeque<>());
    }
    final Collection<DocuSignEnvelopeInformation> events = eventsByEnvelopeId.get(envelopeId);
    events.add(event);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "{envelopeId}", produces = "application/json")
  public ResponseEntity getLastEvent(@PathVariable("envelopeId") String envelopeId) {
    if(!eventsByEnvelopeId.containsKey(envelopeId)) {
      return ResponseEntity.notFound().build();
    }
    final BlockingQueue<DocuSignEnvelopeInformation> events = eventsByEnvelopeId.get(envelopeId);
    return ResponseEntity.ok(events.poll());
  }

}
