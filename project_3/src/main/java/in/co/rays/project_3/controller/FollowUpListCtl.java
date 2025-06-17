
package in.co.rays.project_3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.project_3.dto.BaseDTO;
import in.co.rays.project_3.dto.FollowUpDTO;
import in.co.rays.project_3.dto.FollowUpDTO;
import in.co.rays.project_3.exception.ApplicationException;
import in.co.rays.project_3.model.ModelFactory;
import in.co.rays.project_3.model.FollowUpModelInt;
import in.co.rays.project_3.model.FollowUpModelInt;
import in.co.rays.project_3.util.DataUtility;
import in.co.rays.project_3.util.PropertyReader;
import in.co.rays.project_3.util.ServletUtility;

@WebServlet(name = "FollowUpListCtl", urlPatterns = { "/ctl/FollowUpListCtl" })

public class FollowUpListCtl extends BaseCtl{
	
	@Override
protected void preload(HttpServletRequest request) {
		
		Map<Integer, String> map = new HashMap();
		map.put(1, "dilip");
		map.put(2, "sagar");
		map.put(3, "rahul");
		map.put(4, "ram");
		map.put(5, "bharat");
		
		request.setAttribute("imp", map);	
		
		Map<Integer, String> map1 = new HashMap();
		map1.put(1, "Neurologist");
		map1.put(2, "Dermatologist");
		map1.put(3, "Cardiologist");
		map1.put(4, "Radiologist");
		
		request.setAttribute("productt", map1);	
		
		/*ap<Integer, String> map1 = new HashMap();
		map1.put(1, "Cash");
		map1.put(2, "online");
		map1.put(3, "netbanking");
		map1.put(4, "cheque");
		
		request.setAttribute("FollowUp_typee", map1);
*/
		
		 	}
	
	protected BaseDTO populateDTO(HttpServletRequest request) {
		FollowUpDTO dto = new FollowUpDTO();

		System.out.println(request.getParameter("dob"));

		dto.setId(DataUtility.getLong(request.getParameter("id")));
		dto.setClient(DataUtility.getString(request.getParameter("client")));
		dto.setPhysician(DataUtility.getString(request.getParameter("physician")));
		dto.setAppointmentDate(DataUtility.getDate(request.getParameter("appointmentDate")));
		
        dto.setCharges(DataUtility.getDouble(request.getParameter("charges")));



		populateBean(dto, request);

		return dto;

	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List list;
		List next;
		int pageNo = 1;
		int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));
		FollowUpDTO dto = (FollowUpDTO) populateDTO(request);

		FollowUpModelInt model = ModelFactory.getInstance().getFollowUpModel();
		try {
			list = model.search(dto, pageNo, pageSize);

			ArrayList a = (ArrayList<FollowUpDTO>) list;

			next = model.search(dto, pageNo + 1, pageSize);
			ServletUtility.setList(list, request);
			if (list == null || list.size() == 0) {
				ServletUtility.setErrorMessage("No record found ", request);
			}
			if (next == null || next.size() == 0) {
				request.setAttribute("nextListSize", 0);

			} else {
				request.setAttribute("nextListSize", next.size());
			}
			ServletUtility.setList(list, request);
			ServletUtility.setPageNo(pageNo, request);
			ServletUtility.setPageSize(pageSize, request);
			ServletUtility.forward(getView(), request, response);
		} catch (ApplicationException e) {
			ServletUtility.handleException(e, request, response);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List list = null;
		List next = null;
		int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
		int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

		pageNo = (pageNo == 0) ? 1 : pageNo;
		pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;
		FollowUpDTO dto = (FollowUpDTO) populateDTO(request);
		String op = DataUtility.getString(request.getParameter("operation"));

		String[] ids = request.getParameterValues("ids");
		FollowUpModelInt model = ModelFactory.getInstance().getFollowUpModel();
		try {

			if (OP_SEARCH.equalsIgnoreCase(op)) {
				
		if(request.getParameter("client").equals("") && request.getParameter("physician").equals("")
		&& request.getParameter("appointmentDate").equals("") && request.getParameter("charges").equals("")) {
			ServletUtility.setErrorMessage("fill at least one feild", request);
				}

				if (OP_SEARCH.equalsIgnoreCase(op)) {
					
					pageNo = 1;
				} else if (OP_NEXT.equalsIgnoreCase(op)) {
					pageNo++;
				} else if (OP_PREVIOUS.equalsIgnoreCase(op) && pageNo > 1) {
					pageNo--;
				}

			} else if (OP_NEW.equalsIgnoreCase(op)) {
				ServletUtility.redirect(ORSView.FOLLOWUP_CTL, request, response);
				return;
			} else if (OP_RESET.equalsIgnoreCase(op)) {

				ServletUtility.redirect(ORSView.FOLLOWUP_LIST_CTL, request, response);
				return;
			} else if (OP_DELETE.equalsIgnoreCase(op)) {
				pageNo = 1;
				if (ids != null && ids.length > 0) {
					FollowUpDTO deletedto = new FollowUpDTO();
					for (String id : ids) {
						deletedto.setId(DataUtility.getLong(id));
						model.delete(deletedto);
						ServletUtility.setSuccessMessage("Data Deleted Successfully", request);
					}
				} else {
					ServletUtility.setErrorMessage("Select at least one record", request);
				}
			}
			if (OP_BACK.equalsIgnoreCase(op)) {
				ServletUtility.redirect(ORSView.FOLLOWUP_LIST_CTL, request, response);
				return;
			}
			dto = (FollowUpDTO) populateDTO(request);
			list = model.search(dto, pageNo, pageSize);

			ServletUtility.setDto(dto, request);
			next = model.search(dto, pageNo + 1, pageSize);

			ServletUtility.setList(list, request);
			ServletUtility.setList(list, request);
			if (list == null || list.size() == 0) {
				if (!OP_DELETE.equalsIgnoreCase(op)) {
					ServletUtility.setErrorMessage("No record found ", request);
				}
			}
			if (next == null || next.size() == 0) {
				request.setAttribute("nextListSize", 0);

			} else {
				request.setAttribute("nextListSize", next.size());
			}
			ServletUtility.setList(list, request);
			ServletUtility.setPageNo(pageNo, request);
			ServletUtility.setPageSize(pageSize, request);
			ServletUtility.forward(getView(), request, response);

		} catch (ApplicationException e) {
			ServletUtility.handleException(e, request, response);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getView() {
		// TODO Auto-generated method stub
		return ORSView.FOLLOWUP_LIST_VIEW;
	}



}