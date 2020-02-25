// ----------------------------------------------------------------------------
// CERTI - HLA RunTime Infrastructure
// Copyright (C) 2009-2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi.communication;

/**
 * Class that describes the exception received with <code>CertiMessage</code>.
 * It contains the type and reason of exception.
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 * @version 3.3.3
 */
public class CertiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -982783725846269859L;
	private CertiExceptionType exceptionType;
	private String reason;

	/**
	 * Constructs new <code>CertiException</code> based on supplied parameters
	 *
	 * @param exceptionType
	 * @param reason
	 */
	public CertiException(CertiExceptionType exceptionType, String reason) {
		this.exceptionType = exceptionType;
		this.reason = reason;
	}

	/**
	 * Returns the type of the expection
	 *
	 * @return the type
	 */
	public CertiExceptionType getExceptionType() {
		return exceptionType;
	}

	/**
	 * Returns the reason of exception
	 *
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
}
