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
 *
 * @author aVe
 */
public class CertiException extends Exception {

    private CertiMessageType messageType;
    private CertiExceptionType exceptionType;
    private String reason;

    /**
     *
     * @param messageType
     * @param exceptionType
     * @param reason
     */
    public CertiException(CertiMessageType messageType, CertiExceptionType exceptionType, String reason) {
        this.messageType = messageType;
        this.exceptionType = exceptionType;
        this.reason = reason;
    }

    /**
     *
     * @return
     */
    public CertiMessageType getMessageType() {
        return messageType;
    }

    /**
     *
     * @return
     */
    public CertiExceptionType getExceptionType() {
        return exceptionType;
    }

    /**
     *
     * @return
     */
    public String getReason() {
        return reason;
    }
}
