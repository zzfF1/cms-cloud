INSERT INTO base_assess_version (id, name, branch_type, index_cal_type, status, remark, create_by, create_time,
                                 update_by, update_time)
VALUES (1, '个险2022版本基本法', '1', '10', 1, null, 1, '2024-01-22 17:31:16', 1, '2024-01-22 17:31:19');
INSERT INTO la_assess_config (id, assess_version_id, manage_scope, assess_code, assess_way, assess_type, assess_name,
                              assess_grade, dest_grade, result_grade, result_way, assess_period, data_period,
                              period_parm, cal_order, branch_type, branch_type2, cal_mode_id, handler_class,
                              personnel_conditions, create_by, create_time, update_by, update_time)
VALUES (1, 1, '86', 'G100001', '11', '01', '见习营销员晋升正式营销员', 'G101', 'G102', 'G101', '02', '2', '', '', 1.00,
        '1', '02', 'GXS000101', '', '', null, null, null, null);
INSERT INTO la_assess_config (id, assess_version_id, manage_scope, assess_code, assess_way, assess_type, assess_name,
                              assess_grade, dest_grade, result_grade, result_way, assess_period, data_period,
                              period_parm, cal_order, branch_type, branch_type2, cal_mode_id, handler_class,
                              personnel_conditions, create_by, create_time, update_by, update_time)
VALUES (2, 1, '86', 'G100002', '11', '04', '见习营销员清退', 'G101', 'G000', 'G101', '02', '2', '', '', 2.00, '1', '02',
        'GXS000102', '', '', null, null, null, null);
INSERT INTO la_assess_config (id, assess_version_id, manage_scope, assess_code, assess_way, assess_type, assess_name,
                              assess_grade, dest_grade, result_grade, result_way, assess_period, data_period,
                              period_parm, cal_order, branch_type, branch_type2, cal_mode_id, handler_class,
                              personnel_conditions, create_by, create_time, update_by, update_time)
VALUES (3, 1, '86', 'G1YJ001', '12', '01', '见习营销员晋升正式营销员', 'G101', 'G102', 'G101', '02', '2', '', '', 1.00,
        '1', '02', 'GXS000101', '', '', null, null, null, null);
INSERT INTO la_assess_config (id, assess_version_id, manage_scope, assess_code, assess_way, assess_type, assess_name,
                              assess_grade, dest_grade, result_grade, result_way, assess_period, data_period,
                              period_parm, cal_order, branch_type, branch_type2, cal_mode_id, handler_class,
                              personnel_conditions, create_by, create_time, update_by, update_time)
VALUES (4, 1, '86', 'G1YJ002', '12', '04', '见习营销员清退', 'G101', 'G000', 'G101', '02', '2', '', '', 2.00, '1', '02',
        'GXS000102', '', '', null, null, null, null);
INSERT INTO la_assess_cal_mode (id, assess_version_id, cal_sql, remark, create_by, create_time, update_by, update_time)
VALUES ('GXS000101', 1, '?GA111SUMFYC? >= ?GB111SUMFYC? AND ?GA111NUMPIE? >= ?GB111NUMPIE?',
        '累计FYC>=累计FYC标准 且 件数>=件数标准', 1, '2024-01-22 17:44:24', 1, '2024-01-22 17:44:26');
INSERT INTO la_assess_cal_mode (id, assess_version_id, cal_sql, remark, create_by, create_time, update_by, update_time)
VALUES ('GXS000102', 1, '(?GA111SUMFYC? = ?GB111SUMFYC? AND ?GA111RZMONTHS? >=3) OR ?GA111RZMONTHS? > ?GB111RZMONTHS?',
        '滚动三月累计FYC=标准 或 任职月数 > 任职标准', 1, '2024-01-22 17:44:28', 1, '2024-01-22 17:44:30');
INSERT INTO la_assess_cal_index_config (id, assess_version_id, cal_index_code, assess_way, index_name,
                                        result_table_name, result_table_column_name, cal_order, branch_type,
                                        branch_type2, handler_class, construction_parms, create_by, create_time,
                                        update_by, update_time)
VALUES (1, 1, 'GA111SUMFYC', '11', '考核期累计FYC值', 'LAAssessCalIndexinfo', 'a1', 24.00, '1', '', '', '', 1,
        '2024-01-22 17:47:43', 1, '2024-01-22 17:47:43');
INSERT INTO la_assess_cal_index_config (id, assess_version_id, cal_index_code, assess_way, index_name,
                                        result_table_name, result_table_column_name, cal_order, branch_type,
                                        branch_type2, handler_class, construction_parms, create_by, create_time,
                                        update_by, update_time)
VALUES (2, 1, 'GB111SUMFYC', '11', '考核期累计FYC标准', 'LAAssessCalIndexinfo', 'b1', 24.00, '1', '', '', '', 1,
        '2024-01-22 17:47:43', 1, '2024-01-22 17:47:43');
INSERT INTO la_assess_cal_index_config (id, assess_version_id, cal_index_code, assess_way, index_name,
                                        result_table_name, result_table_column_name, cal_order, branch_type,
                                        branch_type2, handler_class, construction_parms, create_by, create_time,
                                        update_by, update_time)
VALUES (3, 1, 'GA111NUMPIE', '11', '考核期保单件数', 'LAAssessCalIndexinfo', 'a3', 26.00, '1', '', '', '', 1,
        '2024-01-22 17:47:43', 1, '2024-01-22 17:47:43');
INSERT INTO la_assess_cal_index_config (id, assess_version_id, cal_index_code, assess_way, index_name,
                                        result_table_name, result_table_column_name, cal_order, branch_type,
                                        branch_type2, handler_class, construction_parms, create_by, create_time,
                                        update_by, update_time)
VALUES (4, 1, 'GB111NUMPIE', '11', '考核期保单件数标准', 'LAAssessCalIndexinfo', 'b3', 26.00, '1', '', '', '', 1,
        '2024-01-22 17:47:43', 1, '2024-01-22 17:47:43');
INSERT INTO la_assess_cal_index_config (id, assess_version_id, cal_index_code, assess_way, index_name,
                                        result_table_name, result_table_column_name, cal_order, branch_type,
                                        branch_type2, handler_class, construction_parms, create_by, create_time,
                                        update_by, update_time)
VALUES (5, 1, 'GA111RZMONTHS', '11', '任职月数', 'LAAssessCalIndexinfo', 'a4', 27.00, '1', '', '', '', 1,
        '2024-01-22 17:47:43', 1, '2024-01-22 17:47:43');
INSERT INTO la_assess_cal_index_config (id, assess_version_id, cal_index_code, assess_way, index_name,
                                        result_table_name, result_table_column_name, cal_order, branch_type,
                                        branch_type2, handler_class, construction_parms, create_by, create_time,
                                        update_by, update_time)
VALUES (6, 1, 'GB111RZMONTHS', '11', '任职月数标准', 'LAAssessCalIndexinfo', 'b4', 27.00, '1', '', '', '', 1,
        '2024-01-22 17:47:43', 1, '2024-01-22 17:47:43');


INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (1, 1, '01', 'GX101', '入司月数', 'lawage', 'f01', '1', '02', '入司月数', null, 1, '0', '1', '', 1.00, 0, 1.00, 'indEmployMonth', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (2, 1, '01', 'GX001', '首年佣金', 'lawage', 'f02', '1', '02', '首年佣金', 'GXFirstYear', 2, '0', '1', '', 2.00, 1, 2.00, 'indFirstYearCommission', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (4, 1, '01', 'GX002', '续年佣金', 'lawage', 'f03', '1', '02', '续年佣金', null, 2, '0', '1', '', 3.00, 1, 3.00, 'indRenewalYearCommission', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (5, 1, '01', 'GX003', '增员奖', 'lawage', 'f04', '1', '02', '增员奖', null, 2, '0', '1', '', 4.00, 1, 4.00, 'indAddPerson', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (6, 1, '01', 'GX004', '月度绩效', 'lawage', 'f05', '1', '02', '月度绩效', null, 2, '0', '1', '', 5.00, 1, 5.00, 'indPersonalMonthMoney', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (7, 1, '01', 'GX005', '个人季度奖', 'lawage', 'f06', '1', '02', '个人季度奖', null, 2, '0', '1', '', 6.00, 1, 6.00, 'indInitMonthCalBase', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (8, 1, '01', 'GX007', '组管理津贴', 'lawage', 'f07', '1', '02', '组管理津贴', null, 2, '0', '1', '', 7.00, 1, 7.00, 'indInitMonthCalBase', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (9, 1, '01', 'GX008', '部管理津贴', 'lawage', 'f08', '1', '02', '部管理津贴', null, 2, '0', '1', '', 8.00, 1, 8.00, 'indInitMonthCalBase', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (10, 1, '01', 'GX009', '区管理津贴', 'lawage', 'f09', '1', '02', '区管理津贴', null, 2, '0', '1', '', 9.00, 1, 9.00, 'indInitMonthCalBase', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (11, 1, '01', 'GX018', '加扣款总额', 'lawage', 'f10', '1', '02', '加扣款总额', null, 2, '0', '1', '', 10.00, 1, 10.00, 'indAllRewardPunish', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (12, 1, '01', 'GX025', '税前应发', 'lawage', 'f11', '1', '02', '税前应发', null, 2, '0', '1', '', 11.00, 1, 11.00, 'indGxShouldPay', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (15, 1, '01', 'GX026', '增值税', 'lawage', 'k01', '1', '01', '增值税', null, 2, '1', '1', '', 1.00, 1, 21.00, 'indCalTax', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (16, 1, '01', 'GX027', '城建税', 'lawage', 'k02', '1', '01', '城建税', null, 2, '1', '1', '', 2.00, 1, 22.00, 'indCalTax', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (17, 1, '01', 'GX028', '教育费附加税', 'lawage', 'k03', '1', '01', '教育费附加税', null, 2, '1', '1', '', 3.00, 1, 23.00, 'indCalTax', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (18, 1, '01', 'GX029', '地方教育附加税', 'lawage', 'k04', '1', '01', '地方教育附加税', null, 2, '1', '1', '', 4.00, 1, 24.00, 'indCalTax', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (19, 1, '01', 'GX030', '当月城建及教育费合计', 'lawage', 'k05', '1', '01', '当月城建及教育费合计', null, 2, '1', '1', '', 5.00, 1, 25.00, 'indCalTax', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (20, 1, '01', 'GX032', '个人所得税', 'lawage', 'k06', '1', '01', '个人所得税', null, 2, '1', '1', '', 6.00, 1, 26.00, 'indCalTax', null, '');
INSERT INTO wage_calculation_definition (series_no, base_law_id, index_cal_type, cal_code, cal_name, table_name, table_colname, branch_type, wagecal_mode, cal_group_name, cal_process_elem, data_type, wage_type, cal_period, cal_elements, cal_order, out_excel, out_order, handler_class, construction_parm, remark) VALUES (21, 1, '01', 'GX034', '税后实发', 'lawage', 'k07', '1', '01', '税后实发', null, 2, '1', '1', '', 7.00, 1, 27.00, 'indActualPay', null, '');
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10000, 1, 'GXFirstYear', 1, '销售区代码', 4000, 0, 1.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10001, 1, 'GXFirstYear', 2, '区主管编码', 4000, 0, 2.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10002, 1, 'GXFirstYear', 3, '区主管名称', 4000, 0, 3.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10003, 1, 'GXFirstYear', 4, '销售部代码', 4000, 0, 4.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10004, 1, 'GXFirstYear', 5, '部主管编码', 4000, 0, 5.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10005, 1, 'GXFirstYear', 6, '部主管名称', 4000, 0, 6.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10006, 1, 'GXFirstYear', 7, '销售组代码', 4000, 0, 7.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10007, 1, 'GXFirstYear', 8, '组主管编码', 4000, 0, 8.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10008, 1, 'GXFirstYear', 9, '组主管名称', 4000, 0, 9.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10009, 1, 'GXFirstYear', 10, '保单号', 6000, 0, 10.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10010, 1, 'GXFirstYear', 11, '险种编码', 4000, 0, 11.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10011, 1, 'GXFirstYear', 12, '险种名称', 8000, 0, 12.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10012, 1, 'GXFirstYear', 13, '承保日期', 4000, 0, 13.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10013, 1, 'GXFirstYear', 14, '回执日期', 4000, 0, 14.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10014, 1, 'GXFirstYear', 15, '回访日期', 4000, 0, 15.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10015, 1, 'GXFirstYear', 16, '保费', 4000, 1, 16.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10016, 1, 'GXFirstYear', 17, '折标比例', 4000, 4, 17.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10017, 1, 'GXFirstYear', 18, '折标提奖', 4000, 1, 18.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10018, 1, 'GXFirstYear', 19, '提奖比例', 4000, 4, 19.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10019, 1, 'GXFirstYear', 20, 'FYC', 4000, 1, 20.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10020, 1, 'GXFirstYear', 21, '计算标识', 3000, 0, 21.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10021, 1, 'GXFirstYear', 22, '方案代码', 3000, 0, 22.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10022, 1, 'GXFirstYear', 23, '方案名称', 6000, 0, 23.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10023, 1, 'GXFirstYear', 24, '自保件标志', 6000, 0, 24.00);
INSERT INTO wage_cal_elements_config (series_no, base_law_id, cal_elements, row_index, title, col_width, data_type, elem_order) VALUES (10024, 1, 'GXFirstYear', 25, '互保件标志', 6000, 0, 25.00);
