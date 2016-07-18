import re
from django import forms
from django.utils.translation import ugettext as _
from django.core.exceptions import ValidationError

# Describe the structure of the form in the homepage.
class UploadDatasetForm(forms.Form):
    file = forms.FileField(label='')
    algorithm = forms.ChoiceField([('STSC', 'STSC'), \
            ('OPTICSP', 'OPTICS - Python'), \
            ('OPTICSR', 'OPTICS - R'), \
            ('OPTICSJ', 'OPTICS - Java')], \
            widget=forms.RadioSelect(), label='')

# Support function
# Takes in input a regular expression and a string
# Returns true if the string complies with the regular expression
def fullmatch(regex, string):
    """Emulate python-3.4 re.fullmatch()."""
    return re.match("(?:" + regex + r")\Z", string)

# Define the common structure of the forms to change the parameters
# of the different algorithms.
# -- > noisefunctions: Text field to input the function(s) to add to the dataset
# -- > enerateNoise: Checked if new noise has to be generate
# The noise function is validate to respect the format described in the wiki.
class basicForm(forms.Form):
    noiseFunctions = forms.CharField(required=False)
    generateNoise = forms.BooleanField(required=False)

    def clean(self):
        cleaned_data = super(basicForm, self).clean()
        fun = cleaned_data.get("noiseFunctions")

        if (fun != ''):
            # Validate function field
            fun = re.sub('[\s+]', '', fun) #Remove spaces
            # DON'T FUCK UP WITH THIS LINE.
            reg = r"(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\))(\;(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\)))*"

            if not fullmatch(reg, fun) :
                self.add_error("noiseFunctions", "Format not correct")


# Define the structure of the class for the specific parameters of STSC
# It inherits from the basicForm the field for the noise functions
class ParametersSTSC(basicForm):
    numClusters = forms.IntegerField(required=True,min_value=1,label='# Clusters')
    k = forms.IntegerField(required=True,min_value=0)
    simCut = forms.DecimalField(required=True, min_value = 0);
    stop = forms.ChoiceField([(0.01, '0.01'), (0.001, '0.001'), (0.0001, '0.0001'), (0.00001, '0.00001')])

# Define the structure of the class for the specific parameters of OPTICS Python
# It inherits from the basicForm the field for the noise functions
class ParametersOPTICSP(basicForm) :
    minPoints = forms.IntegerField(required=True,min_value=1)
    eps = forms.DecimalField(required=True,min_value=0, decimal_places=2)
    threshold = forms.IntegerField(required=True,min_value=1, max_value=100)

# Define the structure of the class for the specific parameters of OPTICS R
# It inherits from the basicForm the field for the noise functions
class ParametersOPTICSR(basicForm) :
    minPoints = forms.IntegerField(required=True,min_value=1)
    eps = forms.DecimalField(required=True,min_value=0, decimal_places=2)
    angle = forms.IntegerField(required=True,min_value=0, max_value=360)

# Define the structure of the class for the specific parameters of OPTICS J
# It inherits from the basicForm the field for the noise functions
class ParametersOPTICSJ(basicForm) :
    minPoints = forms.IntegerField(required=True,min_value=1)
    eps = forms.DecimalField(required=True,min_value=0, decimal_places=2)
    xi = forms.DecimalField(required=True,min_value=0, max_value=1, decimal_places=2)

