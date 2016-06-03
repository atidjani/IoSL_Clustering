import re
from django import forms
from django.utils.translation import ugettext as _
from django.core.exceptions import ValidationError

class UploadDatasetForm(forms.Form):
    file = forms.FileField(label='')
    algorithm = forms.ChoiceField([('STSC', 'STSC'), ('OPTICS', 'OPTICS')], widget=forms.RadioSelect(), label='')

class ParametersSTSC(forms.Form):
    numClusters = forms.IntegerField(required=True,min_value=1,label='# Clusters')
    k = forms.IntegerField(required=True,min_value=0)
    noiseFunctions = forms.CharField(required=False)
    sigma = forms.FloatField(required=False)
    numPoints = forms.IntegerField(required=False, min_value=0)

    def clean(self):
        cleaned_data = super(ParametersSTSC, self).clean()
        fun = cleaned_data.get("noiseFunctions")
        sig = cleaned_data.get("sigma")
        numPoints = cleaned_data.get("numPoints")

        if (fun != '') and (sig == None):
            self.add_error("sigma", "Sigma not specified")

        if (fun != '') and (numPoints == None):
            self.add_error("numPoints", "Number of noise point not specified")

        # Validate function field
        fun = re.sub('[\s+]', '', fun) #Remove spaces
        # DON'T FUCK UP WITH THIS LINE.
        reg = r"(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?\))(\,(\([x0-9\+\-\*\/\.\ ]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?\)))*"
        if (fun != '') and (not re.fullmatch(reg, fun)) :
            self.add_error("noiseFunctions", "Format not correct")

