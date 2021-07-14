-- Assaf Attias

-------- Question 1 --------
select emp.LastName, emp.FirstName, cEmp.SalaryPerDay, coalesce(proj.Name, '(none)') as projectName, proj.Description
from Employee emp join ConstructorEmployee cEmp on emp.eid = cEmp.eid
left outer join ProjectConstructorEmployee projCEmp on emp.eid = projCEmp.eid
left outer join Project proj on projCEmp.PID = proj.PID;

-------- Question 2 --------
--Official Employees
select emp.*, dept.Name as departmentOrLastProject
from Employee emp
inner join OfficialEmployee oEmp on emp.EID = oEmp.EID
inner join Department dept on oEmp.DepartmentId = dept.DID

UNION

--Constructor Employees
select emp.*, lastProj.Name as departmentOrLastProject
from Employee emp 
inner join constructorEmployee cEmp on emp.EID = cEmp.EID
inner join (--last project of constructor employee
				 select eid, proj.Name
				 from ProjectConstructorEmployee projCEmpOut join Project proj on projCEmpOut.PID = proj.pid
				 where StartWorkingDate >= ALL (select distinct startWorkingDate 
				 							    from ProjectConstructorEmployee projCEmpIn
				   							    where projCEmpIn.EID = projCEmpOut.EID)) lastProj on cEmp.EID = lastProj.EID;
-------- Question 3 --------
select nbd.Name, coalesce(numberOfApartments, 0) as numberOfApartments
from Neighborhood nbd left outer join (--number of apartments in every neighborhood
									   select apt.NeighborhoodID, count(*) as numberOfApartments
									   from apartment apt
									   group by apt.NeighborhoodID) numOfApt
									   on nbd.NID = numOfApt.NeighborhoodID
order by coalesce(numberOfApartments, 0) asc;

-------- Question 4 ---------
select apt.StreetName,apt.Number,apt.Door, rsd.FirstName, rsd.LastName
from Apartment apt left outer join Resident rsd on apt.StreetName = rsd.StreetName
												   and apt.Number = rsd.Number
												   and apt.Door = rsd.door;


-------- Question 5 --------
select *
from ParkingArea
where priceperhour >= All (select distinct priceperhour
						   from ParkingArea);

-------- Question 6 ---------
--the people who paid the maxPricePerDay in the most expensive parking area
select CP.CID, Cars.ID
from CarParking CP join Cars on CP.CID = Cars.CID join ParkingArea PA on CP.ParkingAreaID = PA.AID
where ParkingAreaID in (
						--most expensive parkingArea id
						select AID
						from ParkingArea
						where priceperhour >= All (select distinct priceperhour
												   from ParkingArea)
					   )
and CP.Cost = PA.maxpriceperday;
					
-------- Question 7 --------

select rsd.RID, rsd.FirstName, rsd.LastName
from Resident rsd join Apartment apt on rsd.StreetName = apt.StreetName
										and rsd.Number = apt.Number
										and rsd.Door = apt.Door
join Neighborhood nbdOut on apt.NeighborhoodID = nbdOut.NID
where rsd.RID not in (-- if the resident parked at a neighborhood that he doesn't live at 
					  -- the query will select him, otherwise the query will select nothing
					  select distinct rsd.RID
					  from Resident rsd left outer join Cars on rsd.RID = Cars.ID
					  left outer join CarParking CP on Cars.CID = CP.CID
					  left outer join ParkingArea PA on CP.ParkingAreaID = PA.AID
					  left outer join Neighborhood nbdIn on PA.NeighborhoodID = nbdIn.NID
					  where nbdOut.NID != nbdIn.NID
					  );

-------- Question 8 --------
--all the residents that parked at all of the parkingAreas
select rsd.RID, rsd.FirstName, rsd.LastName
from Resident rsd join Cars on rsd.RID = Cars.ID
join CarParking CP on Cars.CID = CP.CID
join ParkingArea PA on CP.ParkingAreaID = PA.AID
group by rsd.RID, rsd.FirstName, rsd.LastName
having count(distinct PA.AID) = (select count(AID) from ParkingArea);

-------- Question 9 --------
select emp.FirstName, emp.LastName, cEmp.SalaryPerDay
from Employee emp join ConstructorEmployee cEmp on emp.EID = cEmp.EID
where DATEDIFF(year, emp.BirthDate, GETDATE()) > All (select distinct DATEDIFF(year, BirthDate, GETDATE())
													  from Employee emp join OfficialEmployee oEmp on emp.EID = oEmp.EID);
