USE [Retail]
GO
/****** Object:  StoredProcedure [Sync].[Write_tb_AltDepoIsEmri]    Script Date: 05/30/2012 09:59:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


ALTER proc [Sync].[Write_tb_AltDepoIsEmri] 
@str nvarchar(max), @oprType char(1), @serverName varchar(100)
as

begin

if @str is null 
  begin
	select 8 as VersionNumber
	return
  end
  
  declare @strxml xml  
set @strXml = convert(xml, @str)  
declare @AltDepoIsEmriRef int , 
		@AnaDepo dt_depo , 
		@FromAltDepo dt_depo ,
		@ToAltDepo dt_depo ,
		@UrunID int ,
		@Miktar int ,
		@KalanMiktar int ,
		@Tarih datetime,
		@AcilisNedenRef int ,
		@IptalNedenRef int,
		@Durum bit

 SELECT    
  @AltDepoIsEmriRef = Tbl.Col.value('@AltDepoIsEmriRef[1]', 'int'),  
	@AnaDepo	   = Tbl.Col.value('@AnaDepo[1]', 'dt_Depo'),  
	@FromAltDepo	 =   Tbl.Col.value('@FromAltDepo[1]', 'dt_Depo'),
	@ToAltDepo	   = Tbl.Col.value('@ToAltDepo[1]', 'dt_Depo'),
	@UrunID	   = Tbl.Col.value('@UrunID[1]', 'int'),
	@Miktar	   = Tbl.Col.value('@Miktar[1]', 'int'),
	@KalanMiktar	 =  Tbl.Col.value('@KalanMiktar[1]', 'int'),
	@Tarih	   = Tbl.Col.value('@Tarih[1]', 'datetime'),
	@AcilisNedenRef	   = Tbl.Col.value('@AcilisNedenRef[1]', 'int'),
	@IptalNedenRef	   = Tbl.Col.value('@IptalNedenRef[1]', 'int'),
	@Durum	 =  Tbl.Col.value('@Durum[1]', 'bit')
 FROM  @strXml.nodes('//changerow') Tbl(Col)  
 
 

if (@oprType = 'I')
begin

begin try
	insert into tb_AltDepoIsEmri
				(AltDepoIsEmriRef, AnaDepo, FromAltDepo, ToAltDepo, UrunID, Miktar, 
				KalanMiktar, Tarih, AcilisNedenRef, IptalNedenRef, Durum)
	values (@AltDepoIsEmriRef, @AnaDepo, @FromAltDepo, @ToAltDepo, @UrunID, @Miktar, 
				@KalanMiktar, @Tarih, @AcilisNedenRef, @IptalNedenRef, @Durum)
end try 

begin catch 
	if error_number() = 2601
	begin
		update tb_AltDepoIsEmri set FromAltDepo = @FromAltDepo,
				ToAltDepo = @ToAltDepo,
				UrunID = @UrunID,
				Miktar = @Miktar,
				KalanMiktar = @KalanMiktar,
				Tarih = @Tarih,
				AcilisNedenRef = @AcilisNedenRef,
				IptalNedenRef = @IptalNedenRef,
				Durum = @Durum
		where AltDepoIsEmriRef = @AltDepoIsEmriRef and AnaDepo = @AnaDepo
	end
	else 
	begin
		declare @ErrorMessage Varchar(max)
		set @ErrorMessage = Error_Message() 
		raiserror(@ErrorMessage , 16,1)
		return
	end
	
end catch 

end
else if (@oprType = 'U')
begin
	update tb_AltDepoIsEmri set FromAltDepo = @FromAltDepo,
				ToAltDepo = @ToAltDepo,
				UrunID = @UrunID,
				Miktar = @Miktar,
				KalanMiktar = @KalanMiktar,
				Tarih = @Tarih,
				AcilisNedenRef = @AcilisNedenRef,
				IptalNedenRef = @IptalNedenRef,
				Durum = @Durum
	where AltDepoIsEmriRef = @AltDepoIsEmriRef and AnaDepo = @AnaDepo
	
end
else if (@oprType = 'D')
begin
	delete from tb_AltDepoIsEmri where AltDepoIsEmriRef = @AltDepoIsEmriRef and AnaDepo = @AnaDepo
end

end
