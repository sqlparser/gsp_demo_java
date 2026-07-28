USE [Retail]
GO
/****** Object:  StoredProcedure [Sync].[WriteManager]    Script Date: 05/30/2012 09:59:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


ALTER proc [Sync].[WriteManager] (@ProcedureName nvarchar(100) = '',
@XMLStr nvarchar(max) = '', @OprType char(1) = '', @ServerName varchar(100) = '')
as

begin

if @ProcedureName is null  
begin
	Select 4 as VersionNumber
	return
end	
 
declare @execStr nvarchar(max)

set @execStr = 'exec ' + @ProcedureName + ' ''' + @XMLStr+''','''+@OprType+''',''' + 
@ServerName + ''''
--print @execStr
  Begin Try 
		execute (@execStr)
  end try
  begin catch
  
  declare @error_desc varchar(max)
  
  set @error_desc = '
  Message : ' + ERROR_MESSAGE() + ' 
  - ' + 'Error Number : ' + convert(varchar(10), ERROR_NUMBER()) + '
  ' +  'Parameter @XMLStr : ' + @XMLStr + '
  ' + 'Parameter @oprType : ' + @oprType + '
  ' +  'Parameter @serverName : ' + @serverName
  
  
	raiserror (@error_desc,16,1)
	return
  
  end catch 
end

 
 
 
