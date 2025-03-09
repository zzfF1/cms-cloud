create
	function CreateMaxNo(cNoType VARCHAR(50), cNoLimit VARCHAR(20)) returns INT(10) deterministic reads sql data
BEGIN
	DECLARE tMaxNo INTEGER DEFAULT 0;
	update LDMaxNo set MaxNo = MaxNo + 1 where NoType = cNoType and NoLimit = cNoLimit;
	SET tMaxNo = (SELECT MaxNo FROM LDMaxNo where NoType = cNoType and NoLimit = cNoLimit);
	IF tMaxNo = '' || tMaxNo is null THEN
		Insert Into LDMaxNo (NOTYPE, NOLIMIT, MAXNO) values (cNoType, cNoLimit, 1);
		SET tMaxNo = 1;
	END IF;
	RETURN tMaxNo;
END;

create
	function CreateMaxNoStep(cNoType VARCHAR(50), cNoLimit VARCHAR(20), cStep INT(10)) returns INT(10) deterministic reads sql data
BEGIN
	# 根据步长生成序号
	DECLARE tMaxNo INTEGER DEFAULT 0;
	update LDMaxNo set MaxNo = (MaxNo + cStep) where NoType = cNoType and NoLimit = cNoLimit;
	SET tMaxNo = (SELECT MaxNo FROM LDMaxNo where NoType = cNoType and NoLimit = cNoLimit);
	IF tMaxNo = '' || tMaxNo is null THEN
		Insert Into LDMaxNo (NOTYPE, NOLIMIT, MAXNO) values (cNoType, cNoLimit, cStep);
		SET tMaxNo = cStep;
	END IF;
	RETURN tMaxNo;
END;
