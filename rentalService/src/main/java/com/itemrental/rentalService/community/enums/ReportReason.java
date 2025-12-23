package com.itemrental.rentalService.community.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {
  ABUSE("욕설"),
  SPAM("광고/스팸"),
  ILLEGAL("불법 콘텐츠"),
  HATE_SPEECH("혐오 표현"),
  SEXUAL("음란물"),
  OTHER("기타");



  @JsonValue
  private final String description;

  @JsonCreator
  public static ReportReason from(String value){
    for(ReportReason reportReason : ReportReason.values()){
      if(reportReason.description.equals(value)){
        return reportReason;
      }
    }
    return null;
  }

}
