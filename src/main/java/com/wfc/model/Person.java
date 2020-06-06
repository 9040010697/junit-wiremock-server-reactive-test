package com.wfc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

  private String personId;
  private String personName;
  private String fatherName;
  private String dateOfBirth;
  private String mobile;
  private String communicationAddress;
  private String permanentAddress;

}
