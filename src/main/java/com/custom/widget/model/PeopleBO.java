package com.custom.widget.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PeopleBO {

		@JsonProperty("EmailID")
	    private String emailId;

	    @JsonProperty("CreatedTime")
	    private String createdTime;

	    @JsonProperty("Date_of_birth")
	    private String dateOfBirth;

	    @JsonProperty("Second_Reporting_To.ID")
	    private String secondReportingToId;

	    @JsonProperty("Photo")
	    private String photo;

	    @JsonProperty("ApprovalStatus")
	    private String approvalStatus;

	    @JsonProperty("Second_Reporting_To")
	    private String secondReportingTo;

	    @JsonProperty("LocationName")
	    private String locationName;

	    @JsonProperty("Department")
	    private String department;

	    @JsonProperty("ModifiedTime")
	    private String modifiedTime;

	    @JsonProperty("Reporting_To.MailID")
	    private String reportingToMailId;

	    @JsonProperty("Zoho_ID")
	    private long zohoId;

	    @JsonProperty("Designation.ID")
	    private String designationId;

	    @JsonProperty("LocationName.ID")
	    private String locationNameId;

	    @JsonProperty("Mobile.country_code")
	    private String mobileCountryCode;

	    @JsonProperty("Reporting_To")
	    private String reportingTo;

	    @JsonProperty("Photo_downloadUrl")
	    private String photoDownloadUrl;

	    @JsonProperty("Designation")
	    private String designation;

	    @JsonProperty("FirstName")
	    private String firstName;

	    @JsonProperty("Mobile")
	    private String mobile;

	    @JsonProperty("Reporting_To.ID")
	    private String reportingToId;

	    @JsonProperty("Work_phone")
	    private String workPhone;

	    @JsonProperty("Department.ID")
	    private String departmentId;

	    @JsonProperty("LastName")
	    private String lastName;

	    @JsonProperty("EmployeeID")
	    private String employeeId;

	    @JsonProperty("ZUID")
	    private String zuid;

		public String getEmailId() {
			return emailId;
		}

		public void setEmailId(String emailId) {
			this.emailId = emailId;
		}

		public String getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(String createdTime) {
			this.createdTime = createdTime;
		}

		public String getDateOfBirth() {
			return dateOfBirth;
		}

		public void setDateOfBirth(String dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		public String getSecondReportingToId() {
			return secondReportingToId;
		}

		public void setSecondReportingToId(String secondReportingToId) {
			this.secondReportingToId = secondReportingToId;
		}

		public String getPhoto() {
			return photo;
		}

		public void setPhoto(String photo) {
			this.photo = photo;
		}

		public String getApprovalStatus() {
			return approvalStatus;
		}

		public void setApprovalStatus(String approvalStatus) {
			this.approvalStatus = approvalStatus;
		}

		public String getSecondReportingTo() {
			return secondReportingTo;
		}

		public void setSecondReportingTo(String secondReportingTo) {
			this.secondReportingTo = secondReportingTo;
		}

		public String getLocationName() {
			return locationName;
		}

		public void setLocationName(String locationName) {
			this.locationName = locationName;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getModifiedTime() {
			return modifiedTime;
		}

		public void setModifiedTime(String modifiedTime) {
			this.modifiedTime = modifiedTime;
		}

		public String getReportingToMailId() {
			return reportingToMailId;
		}

		public void setReportingToMailId(String reportingToMailId) {
			this.reportingToMailId = reportingToMailId;
		}

		

		public String getDesignationId() {
			return designationId;
		}

		public void setDesignationId(String designationId) {
			this.designationId = designationId;
		}

		public String getLocationNameId() {
			return locationNameId;
		}

		public void setLocationNameId(String locationNameId) {
			this.locationNameId = locationNameId;
		}

		public String getMobileCountryCode() {
			return mobileCountryCode;
		}

		public void setMobileCountryCode(String mobileCountryCode) {
			this.mobileCountryCode = mobileCountryCode;
		}

		public String getReportingTo() {
			return reportingTo;
		}

		public void setReportingTo(String reportingTo) {
			this.reportingTo = reportingTo;
		}

		public String getPhotoDownloadUrl() {
			return photoDownloadUrl;
		}

		public void setPhotoDownloadUrl(String photoDownloadUrl) {
			this.photoDownloadUrl = photoDownloadUrl;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(String designation) {
			this.designation = designation;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getReportingToId() {
			return reportingToId;
		}

		public void setReportingToId(String reportingToId) {
			this.reportingToId = reportingToId;
		}

		public String getWorkPhone() {
			return workPhone;
		}

		public void setWorkPhone(String workPhone) {
			this.workPhone = workPhone;
		}

		public String getDepartmentId() {
			return departmentId;
		}

		public void setDepartmentId(String departmentId) {
			this.departmentId = departmentId;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
		}

		public String getZuid() {
			return zuid;
		}

		public void setZuid(String zuid) {
			this.zuid = zuid;
		}

		public long getZohoId() {
			return zohoId;
		}

		public void setZohoId(long zohoId) {
			this.zohoId = zohoId;
		}

		@Override
		public String toString() {
			return "PeopleBO [emailId=" + emailId + ", createdTime=" + createdTime + ", dateOfBirth=" + dateOfBirth
					+ ", secondReportingToId=" + secondReportingToId + ", photo=" + photo + ", approvalStatus="
					+ approvalStatus + ", secondReportingTo=" + secondReportingTo + ", locationName=" + locationName
					+ ", department=" + department + ", modifiedTime=" + modifiedTime + ", reportingToMailId="
					+ reportingToMailId + ", zohoId=" + zohoId + ", designationId=" + designationId
					+ ", locationNameId=" + locationNameId + ", mobileCountryCode=" + mobileCountryCode
					+ ", reportingTo=" + reportingTo + ", photoDownloadUrl=" + photoDownloadUrl + ", designation="
					+ designation + ", firstName=" + firstName + ", mobile=" + mobile + ", reportingToId="
					+ reportingToId + ", workPhone=" + workPhone + ", departmentId=" + departmentId + ", lastName="
					+ lastName + ", employeeId=" + employeeId + ", zuid=" + zuid + "]";
		}

	

	
}
