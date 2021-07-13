-- Implemented By Assaf Attias

------ Project Deleted --> Constructor Employee Deleted ------
CREATE OR ALTER TRIGGER DeleteProject
ON Project
instead of DELETE
AS

DECLARE @empsToDelete TABLE (eId int);

insert into @empsToDelete
select distinct pce.eid
from ProjectConstructorEmployee pce join deleted on pce.PID = deleted.pid
join ( select eid, count(*) as numProjectsOfEmployee
	   from ProjectConstructorEmployee
	   group by eid
	   having count(*) = 1 ) empsWithOneProject on pce.EID = empsWithOneProject.eid

delete project
where pid in (select pid from deleted)

delete employee
where eid in (select * from @empsToDelete)

GO


CREATE OR ALTER TRIGGER Park
ON CarParking
AFTER UPDATE
AS
if(update(EndTime))
update CarParking
set Cost = 
(case when (convert(float, datediff(minute, StartTime, EndTime))/60) * (select priceperhour from parkingArea where parkingAreaID = AID) > (select maxPricePerDay from parkingArea where parkingAreaID = AID)
then (select maxPricePerDay from parkingArea where parkingAreaID = AID)
else (convert(float, datediff(minute, StartTime, EndTime))/60) * (select priceperhour from parkingArea where parkingAreaID = AID)
end
)
where CID in (select CID from inserted)
and EndTime is not null
and Cost is null

go






CREATE OR ALTER TRIGGER ParkingDiscount
ON CarParking
AFTER UPDATE
AS
if(update(cost))
update CarParking
set Cost = Cost*0.8
where CID in (select inserted.CID from inserted inner join Cars on inserted.CID = Cars.CID inner join Employee emp on Cars.ID = emp.EID)
and StartTime in (select startTime from inserted)
and EndTime is not null
and Cost is not null
go