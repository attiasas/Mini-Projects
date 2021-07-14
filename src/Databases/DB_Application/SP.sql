-- Implemented By Assaf Attias

-- Add Employee
create or alter procedure sp_AddMunicipalEmployee @EID int, 
										 @LastName varchar(255), 
										 @FirstName varchar(255), 
										 @BirthDate Date,
										 @StreetName varchar(255), 
										 @Number int, 
										 @door int, 
										 @City varchar(255)
as
	INSERT INTO Employee (EID,LastName,FirstName,BirthDate,StreetName,Number,door,City)
	VALUES (@EID,@LastName,@FirstName,@BirthDate,@StreetName,@Number,@door,@City);
go

-- Add Official Employee
create or alter procedure sp_AddMunicipalEmployeeOfficial	@EID int, 
													@LastName varchar(255), 
													@FirstName varchar(255), 
													@BirthDate Date,
													@StreetName varchar(255), 
													@Number int, 
													@door int, 
													@City varchar(255),
													@StartDate date,
													@Degree varchar(255),
													@DepartmentId int
as
	exec sp_AddMunicipalEmployee @EID, @lastName, @FirstName, @BirthDate, @StreetName, @Number, @door, @City;

	INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
	VALUES (@EID,@StartDate,@Degree,@DepartmentId);
go

-- Add Constructor Employee
create or alter procedure sp_AddMunicipalEmployeeConstructor	@EID int, 
													@LastName varchar(255), 
													@FirstName varchar(255), 
													@BirthDate Date,
													@StreetName varchar(255), 
													@Number int, 
													@door int, 
													@City varchar(255),
													@CompanyName varchar(255),
													@SalaryPerDay int
as
	exec sp_AddMunicipalEmployee @EID, @lastName, @FirstName, @BirthDate, @StreetName, @Number, @door, @City;

	INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
	VALUES (@EID,@CompanyName,@SalaryPerDay);
go

-- Start Parking
create or alter procedure sp_StartParking	@CID int,
									@StartTime datetime,
									@ParkingAreaID int
as
	INSERT INTO CarParking (CID,StartTime,ParkingAreaID)
	VALUES (@CID,@StartTime,@ParkingAreaID);
go

-- End Parking
create or alter procedure sp_EndParking	@CID int,
								@StartTime datetime,
								@EndTime datetime
as
	UPDATE CarParking
	SET EndTime = @EndTime
	WHERE CID = @CID AND StartTime = @StartTime
go



