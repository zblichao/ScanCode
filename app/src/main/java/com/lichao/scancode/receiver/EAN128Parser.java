package com.lichao.scancode.receiver;

import com.lichao.scancode.entity.NameValuePair;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bo gu on 2/20/2016.
 */
public class EAN128Parser {

    public ArrayList<AI> aiTable;
    public int minAiLength;
    public int maxAiLength;

    protected class AI {
        protected String ai;
        protected String desc;
        protected String dataType;
        protected int aiLength;
        protected int contentLength;
        protected boolean isLengthVariable;

        protected AI (String _ai, String _desc, int _aiLength, String _dataType, int _contentLength, boolean _isLengthVariable) {
            ai = _ai;
            desc = _desc;
            dataType = _dataType;
            contentLength = _contentLength;
            aiLength = _aiLength;
            isLengthVariable = _isLengthVariable;
        }
    }

    public EAN128Parser() {
        aiTable = new ArrayList<AI>();
        aiTable.add(new AI("00", "SerialShippingContainerCode", 2, "Numeric", 18, false));
        aiTable.add(new AI("01", "EAN-NumberOfTradingUnit", 2, "Numeric", 14, false));
        aiTable.add(new AI("02", "EAN-NumberOfTheWaresInTheShippingUnit", 2, "Numeric", 14, false));
        aiTable.add(new AI("10", "LOT", 2, "AlphaNumeric", 20, true));
        aiTable.add(new AI("11", "ProducerDate_JJMMDD", 2, "Numeric", 6, false));
        aiTable.add(new AI("12", "DueDate_JJMMDD", 2, "Numeric", 6, false));
        aiTable.add(new AI("13", "PackingDate_JJMMDD", 2, "Numeric", 6, false));
        aiTable.add(new AI("15", "MinimumDurabilityDate_JJMMDD", 2, "Numeric", 6, false));
        aiTable.add(new AI("17", "expire", 2, "Numeric", 6, false)); //ExpiryDate_JJMMDD
        aiTable.add(new AI("20", "ProductModel", 2, "Numeric", 2, false));
        aiTable.add(new AI("21", "serial_number", 2, "AlphaNumeric", 20, true));
        aiTable.add(new AI("22", "HIBCCNumber", 2, "AlphaNumeric", 29, false));
        aiTable.add(new AI("240", "PruductIdentificationOfProducer", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("241", "CustomerPartsNumber", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("250", "SerialNumberOfAIntegratedModule", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("251", "ReferenceToTheBasisUnit", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("252", "GlobalIdentifierSerialisedForTrade", 3, "Numeric", 2, false));
        aiTable.add(new AI("30", "AmountInParts", 2, "Numeric", 8, true));
        aiTable.add(new AI("310d", "NetWeight_Kilogram", 4, "Numeric", 6, false));
        aiTable.add(new AI("311d", "Length_Meter", 4, "Numeric", 6, false));
        aiTable.add(new AI("312d", "Width_Meter", 4, "Numeric", 6, false));
        aiTable.add(new AI("313d", "Heigth_Meter", 4, "Numeric", 6, false));
        aiTable.add(new AI("314d", "Surface_SquareMeter", 4, "Numeric", 6, false));
        aiTable.add(new AI("315d", "NetVolume_Liters", 4, "Numeric", 6, false));
        aiTable.add(new AI("316d", "NetVolume_CubicMeters", 4, "Numeric", 6, false));
        aiTable.add(new AI("320d", "NetWeight_Pounds", 4, "Numeric", 6, false));
        aiTable.add(new AI("321d", "Length_Inches", 4, "Numeric", 6, false));
        aiTable.add(new AI("322d", "Length_Feet", 4, "Numeric", 6, false));
        aiTable.add(new AI("323d", "Length_Yards", 4, "Numeric", 6, false));
        aiTable.add(new AI("324d", "Width_Inches", 4, "Numeric", 6, false));
        aiTable.add(new AI("325d", "Width_Feed", 4, "Numeric", 6, false));
        aiTable.add(new AI("326d", "Width_Yards", 4, "Numeric", 6, false));
        aiTable.add(new AI("327d", "Heigth_Inches", 4, "Numeric", 6, false));
        aiTable.add(new AI("328d", "Heigth_Feed", 4, "Numeric", 6, false));
        aiTable.add(new AI("329d", "Heigth_Yards", 4, "Numeric", 6, false));
        aiTable.add(new AI("330d", "GrossWeight_Kilogram", 4, "Numeric", 6, false));
        aiTable.add(new AI("331d", "Length_Meter", 4, "Numeric", 6, false));
        aiTable.add(new AI("332d", "Width_Meter", 4, "Numeric", 6, false));
        aiTable.add(new AI("333d", "Heigth_Meter", 4, "Numeric", 6, false));
        aiTable.add(new AI("334d", "Surface_SquareMeter", 4, "Numeric", 6, false));
        aiTable.add(new AI("335d", "GrossVolume_Liters", 4, "Numeric", 6, false));
        aiTable.add(new AI("336d", "GrossVolume_CubicMeters", 4, "Numeric", 6, false));
        aiTable.add(new AI("337d", "KilogramPerSquareMeter", 4, "Numeric", 6, false));
        aiTable.add(new AI("340d", "GrossWeight_Pounds", 4, "Numeric", 6, false));
        aiTable.add(new AI("341d", "Length_Inches", 4, "Numeric", 6, false));
        aiTable.add(new AI("342d", "Length_Feet", 4, "Numeric", 6, false));
        aiTable.add(new AI("343d", "Length_Yards", 4, "Numeric", 6, false));
        aiTable.add(new AI("344d", "Width_Inches", 4, "Numeric", 6, false));
        aiTable.add(new AI("345d", "Width_Feed", 4, "Numeric", 6, false));
        aiTable.add(new AI("346d", "Width_Yards", 4, "Numeric", 6, false));
        aiTable.add(new AI("347d", "Heigth_Inches", 4, "Numeric", 6, false));
        aiTable.add(new AI("348d", "Heigth_Feed", 4, "Numeric", 6, false));
        aiTable.add(new AI("349d", "Heigth_Yards", 4, "Numeric", 6, false));
        aiTable.add(new AI("350d", "Surface_SquareInches", 4, "Numeric", 6, false));
        aiTable.add(new AI("351d", "Surface_SquareFeet", 4, "Numeric", 6, false));
        aiTable.add(new AI("352d", "Surface_SquareYards", 4, "Numeric", 6, false));
        aiTable.add(new AI("353d", "Surface_SquareInches", 4, "Numeric", 6, false));
        aiTable.add(new AI("354d", "Surface_SquareFeed", 4, "Numeric", 6, false));
        aiTable.add(new AI("355d", "Surface_SquareYards", 4, "Numeric", 6, false));
        aiTable.add(new AI("356d", "NetWeight_TroyOunces", 4, "Numeric", 6, false));
        aiTable.add(new AI("357d", "NetVolume_Ounces", 4, "Numeric", 6, false));
        aiTable.add(new AI("360d", "NetVolume_Quarts", 4, "Numeric", 6, false));
        aiTable.add(new AI("361d", "NetVolume_Gallonen", 4, "Numeric", 6, false));
        aiTable.add(new AI("362d", "GrossVolume_Quarts", 4, "Numeric", 6, false));
        aiTable.add(new AI("363d", "GrossVolume_Gallonen", 4, "Numeric", 6, false));
        aiTable.add(new AI("364d", "NetVolume_CubicInches", 4, "Numeric", 6, false));
        aiTable.add(new AI("365d", "NetVolume_CubicFeet", 4, "Numeric", 6, false));
        aiTable.add(new AI("366d", "NetVolume_CubicYards", 4, "Numeric", 6, false));
        aiTable.add(new AI("367d", "GrossVolume_CubicInches", 4, "Numeric", 6, false));
        aiTable.add(new AI("368d", "GrossVolume_CubicFeet", 4, "Numeric", 6, false));
        aiTable.add(new AI("369d", "GrossVolume_CubicYards", 4, "Numeric", 6, false));
        aiTable.add(new AI("37", "QuantityInParts", 2, "Numeric", 8, true));
        aiTable.add(new AI("390d", "AmountDue_DefinedValutaBand", 4, "Numeric", 15, true));
        aiTable.add(new AI("391d", "AmountDue_WithISOValutaCode", 4, "Numeric", 18, true));
        aiTable.add(new AI("392d", "BePayingAmount_DefinedValutaBand", 4, "Numeric", 15, true));
        aiTable.add(new AI("393d", "BePayingAmount_WithISOValutaCode", 4, "Numeric", 18, true));
        aiTable.add(new AI("400", "JobNumberOfGoodsRecipient", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("401", "ShippingNumber", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("402", "DeliveryNumber", 3, "Numeric", 17, false));
        aiTable.add(new AI("403", "RoutingCode", 3, "AlphaNumeric", 30, true));
        aiTable.add(new AI("410", "EAN_UCC_GlobalLocationNumber(GLN)_GoodsRecipient", 3, "Numeric", 13, false));
        aiTable.add(new AI("411", "EAN_UCC_GlobalLocationNumber(GLN)_InvoiceRecipient", 3, "Numeric", 13, false));
        aiTable.add(new AI("412", "EAN_UCC_GlobalLocationNumber(GLN)_Distributor", 3, "Numeric", 13, false));
        aiTable.add(new AI("413", "EAN_UCC_GlobalLocationNumber(GLN)_FinalRecipient", 3, "Numeric", 13, false));
        aiTable.add(new AI("414", "EAN_UCC_GlobalLocationNumber(GLN)_PhysicalLocation", 3, "Numeric", 13, false));
        aiTable.add(new AI("415", "EAN_UCC_GlobalLocationNumber(GLN)_ToBilligParticipant", 3, "Numeric", 13, false));
        aiTable.add(new AI("420", "ZipCodeOfRecipient_withoutCountryCode", 3, "AlphaNumeric", 20, true));
        aiTable.add(new AI("421", "ZipCodeOfRecipient_withCountryCode", 3, "AlphaNumeric", 12, true));
        aiTable.add(new AI("422", "BasisCountryOfTheWares_ISO3166Format", 3, "Numeric", 3, false));
        aiTable.add(new AI("7001", "Nato Stock Number", 4, "Numeric", 13, false));
        aiTable.add(new AI("8001", "RolesProducts", 4, "Numeric", 14, false));
        aiTable.add(new AI("8002", "SerialNumberForMobilePhones", 4, "AlphaNumeric", 20, true));
        aiTable.add(new AI("8003", "GlobalReturnableAssetIdentifier", 4, "AlphaNumeric", 34, true));
        aiTable.add(new AI("8004", "GlobalIndividualAssetIdentifier", 4, "Numeric", 30, true));
        aiTable.add(new AI("8005", "SalesPricePerUnit", 4, "Numeric", 6, false));
        aiTable.add(new AI("8006", "IdentifikationOfAProductComponent", 4, "Numeric", 18, false));
        aiTable.add(new AI("8007", "IBAN", 4, "AlphaNumeric", 30, true));
        aiTable.add(new AI("8008", "DataAndTimeOfManufacturing", 4, "Numeric", 12, true));
        aiTable.add(new AI("8018", "GlobalServiceRelationNumber", 4, "Numeric", 18, false));
        aiTable.add(new AI("8020", "NumberBillCoverNumber", 4, "AlphaNumeric", 25, false));
        aiTable.add(new AI("8100", "CouponExtendedCode_NSC_offerCcode", 4, "Numeric", 10, false));
        aiTable.add(new AI("8101", "CouponExtendedCode_NSC_offerCcode_EndOfOfferCode", 4, "Numeric", 14, false));
        aiTable.add(new AI("8102", "CouponExtendedCode_NSC", 4, "Numeric", 6, false));
        aiTable.add(new AI("90", "InformationForBilateralCoordinatedApplications", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("91", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("92", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("93", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("94", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("95", "serial_number", 2, "AlphaNumeric", 30, true)); // Company specific
        aiTable.add(new AI("96", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("97", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("98", "Company specific", 2, "AlphaNumeric", 30, true));
        aiTable.add(new AI("99", "Company specific", 2, "AlphaNumeric", 30, true));

        AI ai;
        int i;
        minAiLength = 10;
        maxAiLength = 0;

        for (i = 0; i < aiTable.size(); i ++) {
            ai = aiTable.get(i);
            if (ai.aiLength > maxAiLength) maxAiLength = ai.aiLength;
            if (ai.aiLength < minAiLength) minAiLength = ai.aiLength;
        }
    }

    public int getAI(String ai) {
        int i;
        for (i = 0; i < aiTable.size(); i ++) {
            if (aiTable.get(i).ai.equals(ai)) return i;
        }
        return -1;
    }

    public int getFirstAIFromBarcode(String barcode) {
        int i, aiIndex;
        for (i = minAiLength; i <= maxAiLength; i ++) {
            aiIndex = getAI(barcode.substring(0, i));
            if (aiIndex != -1) return aiIndex;
        }
        return -1;
    }

    public int getContentLength(String barcode) {
        int aiIndex, i, whiteSpace;
        char[] chars;
        aiIndex = getFirstAIFromBarcode(barcode);
        if (aiIndex != -1) {
            AI temp = aiTable.get(aiIndex);

            chars = barcode.substring(temp.aiLength).toCharArray();
            for (i = 0; i < chars.length; i ++) {
                if (Character.isLetterOrDigit(chars[i])) break;
                if (chars[i] == 29) chars[i] = 'a';
            }
            whiteSpace = i;

            if (temp.isLengthVariable == false) {
                return (temp.contentLength + temp.aiLength + whiteSpace);
            }
            else {
                int lengthToRead = Math.min(temp.contentLength + whiteSpace, chars.length);
                String result = String.valueOf(chars).substring(0, lengthToRead);
                int indexOfGroupTermination = result.indexOf((char) 29);
                if (indexOfGroupTermination >= 0) lengthToRead = indexOfGroupTermination + 1;
                return lengthToRead + temp.aiLength;
            }
        }
        return 0;
    }

    public ArrayList<NameValuePair> parseBarcodeToList(String barcode) throws Exception{
        ArrayList<NameValuePair> results = new ArrayList<NameValuePair>();
        AI aiTemp;

        int contentLength;
        String aiContentPair, content;

        barcode = barcode.replaceAll("[^A-Za-z0-9]", "" + (char) 29);
        barcode = barcode.trim();
        while (!barcode.isEmpty()) {
            contentLength = getContentLength(barcode);
            aiContentPair = barcode.substring(0, contentLength);
            aiTemp = aiTable.get(getFirstAIFromBarcode(aiContentPair));
            content = aiContentPair.substring(aiTemp.aiLength);
            if (!aiTemp.ai.equals("17")) results.add(new NameValuePair(aiTemp.desc, content.trim()));
            else {
                SimpleDateFormat format_src = new SimpleDateFormat("yyMMdd");
                SimpleDateFormat format_dest = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    results.add(new NameValuePair(aiTemp.desc, format_dest.format(format_src.parse(content.trim()))));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            barcode = barcode.substring(contentLength).trim();
        }

        return results;
    }

    public String getPrimary(String barcode) {
        return barcode.substring(0, 16);
    }

    public String getSecondary(String barcode) {
        return barcode.substring(16);
    }

    public String getProductCode(String barcodePrimary) {
        return barcodePrimary.substring(11, 15);
    }

    public String getManufacterCode(String barcodePrimary) {
        return barcodePrimary.substring(3, 11);
    }

}
