-- Implemented By Assaf Attias

---- Get Constructor Employee Over 50 (age) ----
create or alter view ConstructionEmployeeOverFifty 
as
select emp.*, cEmp.CompanyName, cEmp.SalaryPerDay
from Employee emp join ConstructorEmployee cEmp on emp.EID = cEmp.EID
where DATEDIFF(day, emp.BirthDate, GETDATE())/365 >= 50;
go

---- Get the Number of Apartments in Neighborhood ----
create or alter view ApartmentNumberInNeighborhood
as
select nbd.NID, coalesce(ApartmentNumber, 0) as ApartmentNumber
from Neighborhood nbd left outer join (--number of apartments in every neighborhood
									   select apt.NeighborhoodID, count(*) as ApartmentNumber
									   from apartment apt
									   group by apt.NeighborhoodID) numOfApt
									   on nbd.NID = numOfApt.NeighborhoodID
go
---- Get the car that parked the most in each parking area ----
create or alter view MaxParking
as
select ParkingAreaID, CID, count(*) as MaxParkingCount
from carParking cpOut
group by ParkingAreaID, CID
having count(*) >= ALL ( select count(*) as numCarParkedInArea
						 from carParking cpIn
						 where cpOut.ParkingAreaID = cpIn.ParkingAreaID
						 group by ParkingAreaID, CID);
go