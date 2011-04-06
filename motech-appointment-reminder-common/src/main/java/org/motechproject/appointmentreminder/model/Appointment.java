/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.appointmentreminder.model;

import java.util.Date;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

public class Appointment extends MotechAuditableDataObject {

	private static final long serialVersionUID = -3065277684319403907L;
	@TypeDiscriminator
	private String patientId;
	private Date reminderWindowStart;
	private Date reminderWindowEnd;
	private Date date;
	
	
	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}
	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	/**
	 * Get the value for when the appointment reminder should stop for this appointment
	 * @return the reminderWindowStart
	 */
	public Date getReminderWindowStart() {
		return reminderWindowStart;
	}
	/**
	 * Set the value for when the appointment reminder should start for this appointment
	 * @param reminderWindowStart the reminderWindowStart to set
	 */
	public void setReminderWindowStart(Date reminderWindowStart) {
		this.reminderWindowStart = reminderWindowStart;
	}
	/**
	 * Get the value for when the appointment reminder should start for this appointment
	 * @return the reminderWindowEnd
	 */
	public Date getReminderWindowEnd() {
		return reminderWindowEnd;
	}
	/**
	 * Set the value for when the appointment reminder should stop for this appointment
	 * @param reminderWindowEnd the reminderWindowEnd to set
	 */
	public void setReminderWindowEnd(Date reminderWindowEnd) {
		this.reminderWindowEnd = reminderWindowEnd;
	}
	/**
	 * Get the specific appointment date
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * Set the specific appointment date
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
    @Override
    public String toString() {
        return "id = " + this.getId() + ", reminder window start = " + reminderWindowStart + ", reminder window end = " + this.reminderWindowEnd + ", appointment date = " + this.date + ", patient id = " + patientId; 
    }
    
    @Override
    public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Appointment a = (Appointment) o;
	    if (this.getId() != null ? !this.getId().equals(a.getId()) : a.getId() != null) return false;
	    if (this.reminderWindowStart != null ? !this.reminderWindowStart.equals(a.getReminderWindowStart()) : a.getReminderWindowStart() != null) return false;
	    if (this.reminderWindowEnd != null ? !this.reminderWindowEnd.equals(a.getReminderWindowEnd()) : a.getReminderWindowEnd() != null) return false;
	    if (this.date != null ? !this.date.equals(a.getDate()) : a.getDate() != null) return false;
	    if (this.patientId != null ? !this.patientId.equals(a.getPatientId()) : a.getPatientId() != null) return false;
	    
        return true;
    }

    @Override
    public int hashCode() {
	    int result = this.getId() != null ? this.getId().hashCode() : 0;
	    result = 31 * result + (this.reminderWindowStart != null ? this.reminderWindowStart.hashCode() : 0);
	    result = 31 * result + (this.reminderWindowEnd != null ? this.reminderWindowEnd.hashCode() : 0);
	    result = 31 * result + (this.date != null ? this.date.hashCode() : 0);
	    result = 31 * result + (this.patientId != null ? this.patientId.hashCode() : 0);
	    return result;
    }
	
	
}