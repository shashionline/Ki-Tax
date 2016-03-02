package ch.dvbern.ebegu.api.util.errors;

import ch.dvbern.ebegu.api.util.validation.EbeguExceptionReport;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguNotFoundException;
import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Iterator;
import java.util.List;

/**
 * Created by imanol on 01.03.16.
 */
@Provider
public class EbeguExceptionMapper implements ExceptionMapper<EbeguException> {

	@Override
	public Response toResponse(EbeguException exception) {
		if (exception instanceof EbeguNotFoundException) {
			EbeguNotFoundException ebeguNotFoundException = EbeguNotFoundException.class.cast(exception);
//			Exception e = ebeguNotFoundException.getException();
//			if (e != null) {
//				return buildResponse(unwrapException(e), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
//			} else if (ebeguNotFoundException.getReturnValueViolations().size() == 0) {
				return buildViolationReportResponse(ebeguNotFoundException, Status.BAD_REQUEST);
//			} else {
//				return buildViolationReportResponse(ebeguNotFoundException, Status.INTERNAL_SERVER_ERROR);
//			}
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}

	protected Response buildResponse(Object entity, String mediaType, Status status) {
		ResponseBuilder builder = Response.status(status).entity(entity);
		builder.type(MediaType.TEXT_PLAIN);
		builder.header(Validation.VALIDATION_HEADER, "true");
		return builder.build();
	}

	protected Response buildViolationReportResponse(EbeguException exception, Status status) {
		ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");

		// Check standard media types.
		MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
		if (mediaType != null) {
			builder.type(mediaType);
			builder.entity(new EbeguExceptionReport(exception));
			return builder.build();
		}

		// Default media type.
		builder.type(MediaType.TEXT_PLAIN);
		builder.entity(exception.toString());
		return builder.build();
	}

	protected String unwrapException(Throwable t) {
		StringBuffer sb = new StringBuffer();
		doUnwrapException(sb, t);
		return sb.toString();
	}

	private void doUnwrapException(StringBuffer sb, Throwable t) {
		if (t == null) {
			return;
		}
		sb.append(t.toString());
		if (t.getCause() != null && t != t.getCause()) {
			sb.append('[');
			doUnwrapException(sb, t.getCause());
			sb.append(']');
		}
	}

	private MediaType getAcceptMediaType(List<MediaType> accept) {
		Iterator<MediaType> it = accept.iterator();
		while (it.hasNext()) {
			MediaType mt = it.next();
            /*
             * application/xml media type causes an exception:
             * org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure: Could not find MessageBodyWriter for response
             * object of type: org.jboss.resteasy.api.validation.ViolationReport of media type: application/xml
             * Not anymore
             */
			if (MediaType.APPLICATION_XML_TYPE.getType().equals(mt.getType())
				&& MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mt.getSubtype())) {
				return MediaType.APPLICATION_XML_TYPE;
			}
			if (MediaType.APPLICATION_JSON_TYPE.getType().equals(mt.getType())
				&& MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mt.getSubtype())) {
				return MediaType.APPLICATION_JSON_TYPE;
			}
		}
		return null;
	}

}

